import {
  Directive,
  effect,
  inject,
  input,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';

@Directive({
  selector: '[showIfAuthenticated]',
})
export class ShowIfAuthenticatedDirective {
  private readonly _templateRef = inject(TemplateRef);
  private readonly _viewContainerRef = inject(ViewContainerRef);

  private _hasView = false;
  public showIfAuthenticated = input.required<boolean>();

  constructor() {
    effect(() => {
      const shouldShow = this.showIfAuthenticated();
      if (shouldShow && !this._hasView) {
        this._viewContainerRef.createEmbeddedView(this._templateRef);
        this._hasView = true;
      } else {
        this._viewContainerRef.clear();
        this._hasView = false;
      }
    });
  }
}
