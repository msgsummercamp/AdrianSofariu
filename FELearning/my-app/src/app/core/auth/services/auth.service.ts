import { computed, inject, Injectable, Signal, signal } from '@angular/core';
import { AuthState } from '../models/auth-state';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoginResponse } from '../models/loginResponse';

const initialState: AuthState = {
  isAuthenticated: false,
  username: '',
  token: '',
  roles: [],
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  public isLoggedIn: Signal<boolean>;
  public username: Signal<string>;
  public authToken: Signal<string>;
  private readonly _authState = signal<AuthState>(initialState);
  private readonly _httpClient = inject(HttpClient);
  private readonly _router = inject(Router);
  private readonly API_URL: string = 'http://localhost:8080/api/v1';

  constructor() {
    const savedState = this.loadAuthState();
    this._authState.set(savedState);

    this.isLoggedIn = computed(() => this._authState().isAuthenticated);
    this.username = computed(() => this._authState().username);
    this.authToken = computed(() => this._authState().token);
  }

  public logIn(username: string, password: string): void {
    const payload = { username, password };

    this._httpClient
      .post<LoginResponse>(`${this.API_URL}/auth/signin`, payload)
      .subscribe({
        next: (response) => {
          this._authState.update(() => ({
            isAuthenticated: true,
            username: username,
            token: response.token,
            roles: response.roles,
          }));
          this.saveAuthState(this._authState());
          this._router.navigate(['/profile']);
        },
        error: (err) => {
          console.error('Login failed:', err);
        },
      });
  }

  public logOut(): void {
    this._authState.set({
      isAuthenticated: false,
      username: '',
      token: '',
      roles: [],
    });
    console.log('User logged out successfully.');
    this.clearAuthState();
    this._router.navigate(['/login']);
  }

  private saveAuthState(state: AuthState): void {
    localStorage.setItem('authState', JSON.stringify(state));
  }

  private loadAuthState(): AuthState {
    const savedState = localStorage.getItem('authState');
    return savedState ? JSON.parse(savedState) : initialState;
  }

  private clearAuthState(): void {
    localStorage.removeItem('authState');
  }
}
