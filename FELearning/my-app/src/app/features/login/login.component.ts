import { Component, inject } from '@angular/core';
import {
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../core/auth/services/auth.service';
import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

type LoginForm = {
  username: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
};

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private readonly _authService = inject(AuthService);
  private readonly _formBuilder = inject(NonNullableFormBuilder);

  protected readonly loginFormGroup = this._formBuilder.group<LoginForm>({
    username: this._formBuilder.control('', [
      Validators.minLength(5),
      Validators.required,
    ]),
    email: this._formBuilder.control('', [
      Validators.email,
      Validators.required,
    ]),
    password: this._formBuilder.control('', [
      Validators.minLength(5),
      Validators.required,
    ]),
  });

  protected onFormSubmit(): void {
    if (this.loginFormGroup.valid) {
      console.log('Raw values: ', this.loginFormGroup.getRawValue());
      this._authService.logIn(
        this.loginFormGroup.controls.username.value,
        this.loginFormGroup.controls.password.value,
      );
    }
  }
}
