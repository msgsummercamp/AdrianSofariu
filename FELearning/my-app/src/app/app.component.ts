import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { AuthGuardDirective } from './auth-guard.directive';
import { NotFoundComponent } from './not-found/not-found.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    AuthGuardDirective,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    NotFoundComponent,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  public readonly title = 'my-app';
  public readonly loggedIn = signal<boolean>(false);

  public mockLogin(): void {
    this.loggedIn.set(!this.loggedIn());
  }
}
