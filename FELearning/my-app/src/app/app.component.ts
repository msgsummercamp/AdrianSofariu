import { Component, inject, signal } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { AuthGuardDirective } from './auth-guard.directive';
import { NotFoundComponent } from './not-found/not-found.component';
import { AuthService } from './services/auth.service';
import { UppercasePipe } from './pipes/uppercase.pipe';
import { TimesfivePipe } from './pipes/timesfive.pipe';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    AuthGuardDirective,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    NotFoundComponent,
    RouterModule,
    RouterOutlet,
    UppercasePipe,
    TimesfivePipe,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [TimesfivePipe, UppercasePipe],
})
export class AppComponent {
  public readonly title = 'my-app';

  private readonly authService = inject(AuthService);

  public toggleLogin(): void {
    this.authService.toggleAuthState();
  }

  public isAuthenticated(): boolean {
    return this.authService.isLoggedIn();
  }

  public getUsername(): string {
    return this.authService.username();
  }
}
