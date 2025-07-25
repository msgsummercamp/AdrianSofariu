import { AuthService } from './core/auth/services/auth.service';
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { IfAuthenticatedDirective } from './core/auth/directives/auth-guard.directive';
import { UpperCasePipe } from '@angular/common';
import { IfNotAuthenticatedDirective } from './core/auth/directives/not-auth.directive';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    IfAuthenticatedDirective,
    IfNotAuthenticatedDirective,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    UpperCasePipe,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  public readonly title = 'my-app';

  private readonly authService = inject(AuthService);

  public isAuthenticated(): boolean {
    return this.authService.isLoggedIn();
  }

  public getUsername(): string {
    return this.authService.username();
  }

  public logOut(): void {
    this.authService.logOut();
  }
}
