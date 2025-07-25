import {
  HttpHandlerFn,
  HttpHeaderResponse,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
) => {
  const authToken = inject(AuthService).authToken();
  const SIGN_IN_URL_MATCHER = '/auth/signin';

  if (req.url.includes(SIGN_IN_URL_MATCHER)) {
    return next(req);
  }

  const clonedRequest = req.clone({
    setHeaders: {
      Authorization: `Bearer ${authToken}`,
    },
  });
  return next(clonedRequest);
};
