import { computed, Injectable, Signal, signal } from '@angular/core';
import { AuthState } from '../types/authState';

const initialState: AuthState = {
  isAuthenticated: false,
  username: '',
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  public isLoggedIn: Signal<boolean>;
  public username: Signal<string>;
  private readonly _authState = signal<AuthState>(initialState);

  constructor() {
    this.isLoggedIn = computed(() => this._authState().isAuthenticated);
    this.username = computed(() => this._authState().username);
  }

  public toggleAuthState(): void {
    const newAuthState = !this._authState().isAuthenticated;
    let newUsername = '';
    if (this._authState().username === '') newUsername = 'randomUser199';
    this._authState.update((state) => ({
      ...state,
      isAuthenticated: newAuthState,
      username: newUsername,
    }));
  }
}
