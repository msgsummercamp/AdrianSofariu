import { computed, Injectable, Signal, signal } from '@angular/core';
import { AuthState } from '../types/authState';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  public isLoggedIn: Signal<boolean>;
  private readonly _authState = signal<AuthState>({ isAuthenticated: false });

  constructor() {
    this.isLoggedIn = computed(() => this._authState().isAuthenticated);
  }

  public toggleAuthState(): void {
    const newState = !this._authState().isAuthenticated;
    this._authState.update((state) => ({
      ...state,
      isAuthenticated: newState,
    }));
  }
}
