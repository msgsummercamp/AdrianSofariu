import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { AuthGuardDirective } from './auth-guard.directive';
import { AuthService } from './services/auth.service';
import { UppercasePipe } from './pipes/uppercase.pipe';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    AuthGuardDirective,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    UppercasePipe,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [UppercasePipe],
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
