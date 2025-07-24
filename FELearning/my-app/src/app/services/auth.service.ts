import { computed, Injectable, Signal, signal } from '@angular/core';
import { AuthState } from '../types/authState';

const initialState: AuthState = {
  isAuthenticated: false,
  username: '',
  token: '',
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  public isLoggedIn: Signal<boolean>;
  public username: Signal<string>;
  public authToken: Signal<string>;
  private readonly _authState = signal<AuthState>(initialState);

  constructor() {
    this.isLoggedIn = computed(() => this._authState().isAuthenticated);
    this.username = computed(() => this._authState().username);
    this.authToken = computed(() => this._authState().token);
  }

  public toggleAuthState(): void {
    const newAuthState = !this._authState().isAuthenticated;
    let newUsername = '';
    let newToken = '';
    if (this._authState().username === '') {
      newUsername = 'randomUser199';
      newToken = 'dadawd12312edawfgawdawt2e2eh3WDAWg5awd';
    }

    this._authState.update((state) => ({
      isAuthenticated: newAuthState,
      username: newUsername,
      token: newToken,
    }));
  }
}
