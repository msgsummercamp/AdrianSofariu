import {
  Directive,
  effect,
  ElementRef,
  inject,
  input,
  InputSignal,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';

@Directive({
  selector: '[authGuard]',
})
export class AuthGuardDirective {
  private readonly elementRef: ElementRef<HTMLElement> = inject(ElementRef);

  public readonly authGuard: InputSignal<boolean> = input.required<boolean>();

  constructor() {
    effect(() => {
      const isAuthenticated = this.authGuard();
      if (isAuthenticated) {
        this.elementRef.nativeElement.classList.remove('hidden');
      } else {
        this.elementRef.nativeElement.classList.add('hidden');
      }
    });
  }
}
