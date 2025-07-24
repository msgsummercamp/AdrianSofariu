import { Component, inject, signal } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { AuthGuardDirective } from './auth-guard.directive';
import { NotFoundComponent } from './not-found/not-found.component';
import { AuthService } from './services/auth.service';

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
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
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
}
