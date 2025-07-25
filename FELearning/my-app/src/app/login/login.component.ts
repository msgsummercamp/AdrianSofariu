import { Component, inject } from '@angular/core';
import {
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

type LoginForm = {
  username: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
};

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private readonly _formBuilder = inject(NonNullableFormBuilder);

  protected readonly loginFormGroup = this._formBuilder.group<LoginForm>({
    username: this._formBuilder.control('', Validators.minLength(5)),
    email: this._formBuilder.control('', Validators.email),
    password: this._formBuilder.control('', Validators.minLength(5)),
  });

  protected onFormSubmit(): void {
    if (this.loginFormGroup.valid) {
      console.log('Raw values: ', this.loginFormGroup.getRawValue());
    }
  }
}
