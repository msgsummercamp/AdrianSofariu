import { inject } from '@angular/core';
import { CanActivateFn, RedirectCommand, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authenticatedGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const requiresAuth = route.data['requiresAuth'] as boolean;
  const isAuthenticated = authService.isLoggedIn();

  if (requiresAuth && isAuthenticated) {
    return true;
  } else if (!requiresAuth && !isAuthenticated) {
    return true;
  }

  return requiresAuth
    ? new RedirectCommand(router.parseUrl('/login'))
    : new RedirectCommand(router.parseUrl('/profile'));
};
