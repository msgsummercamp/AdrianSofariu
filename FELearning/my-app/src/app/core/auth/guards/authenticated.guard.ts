import { inject } from '@angular/core';
import { CanActivateFn, RedirectCommand, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authenticatedGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const routeName = route.routeConfig?.path;
  const isAuthenticated = authService.isLoggedIn();

  if (routeName === 'login' && isAuthenticated) {
    return new RedirectCommand(router.parseUrl('/home'));
  } else if (routeName === 'profile' && !isAuthenticated) {
    return new RedirectCommand(router.parseUrl('/login'));
  }

  return true;
};
