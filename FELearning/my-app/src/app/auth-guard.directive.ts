import {
  Directive,
  effect,
  inject,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';
import { AuthService } from './services/auth.service';

@Directive({
  selector: '[ifAuthenticated]',
})
export class IfAuthenticatedDirective {
  private readonly _templateRef = inject(TemplateRef);
  private readonly _viewContainerRef = inject(ViewContainerRef);
  private readonly _authService = inject(AuthService);

  private _hasView = false;

  constructor() {
    effect(() => {
      const isAuthenticated = this._authService.isLoggedIn();
      if (isAuthenticated && !this._hasView) {
        this._viewContainerRef.createEmbeddedView(this._templateRef);
        this._hasView = true;
      } else {
        this._viewContainerRef.clear();
        this._hasView = false;
      }
    });
  }
}
