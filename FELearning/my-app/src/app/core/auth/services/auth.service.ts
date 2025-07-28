import { computed, inject, Injectable, Signal, signal } from '@angular/core';
import { AuthState } from '../models/auth-state';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoginResponse } from '../models/loginResponse';
import { CookieService } from 'ngx-cookie-service';
import { jwtDecode } from 'jwt-decode';
import { DecodedToken } from '../models/decoded-token';
import { environment } from '../../../../environments/environment';

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
  private readonly _cookieService = inject(CookieService);
  private readonly API_URL: string = environment.API_URL;

  constructor() {
    this.restoreAuthState();

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
          const token = response.token;
          this.saveTokenToSessionStorage(token);
          this.decodeAndSetAuthState(token);
          this._router.navigate(['/profile']);
        },
        error: (err) => {
          console.error('Login failed:', err);
        },
      });
  }

  public logOut(): void {
    this.clearTokenFromSessionStorage();
    this._authState.set({
      isAuthenticated: false,
      username: '',
      token: '',
      roles: [],
    });
    this._router.navigate(['/login']);
  }

  private saveTokenToSessionStorage(token: string): void {
    sessionStorage.setItem('authToken', token);
  }

  private getTokenFromSessionStorage(): string | null {
    return sessionStorage.getItem('authToken');
  }

  private clearTokenFromSessionStorage(): void {
    sessionStorage.removeItem('authToken');
  }

  private decodeAndSetAuthState(token: string): void {
    try {
      const decodedToken: DecodedToken = jwtDecode(token);
      this._authState.set({
        isAuthenticated: true,
        username: decodedToken.sub,
        token,
        roles: decodedToken.roles,
      });
    } catch (err) {
      console.error('Failed to decode token:', err);
      this._authState.set({
        isAuthenticated: false,
        username: '',
        token: '',
        roles: [],
      });
    }
  }

  private restoreAuthState(): void {
    const token = this.getTokenFromSessionStorage();
    if (token) {
      this.decodeAndSetAuthState(token);
    }
  }
}
