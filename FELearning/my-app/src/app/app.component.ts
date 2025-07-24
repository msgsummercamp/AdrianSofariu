import { Component, signal } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { NotFoundComponent } from './not-found/not-found.component';
import { AuthGuardDirective } from './auth-guard.directive';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterModule,
    MatButtonModule,
    MatToolbarModule,
    MatIconModule,
    NotFoundComponent,
    AuthGuardDirective,
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
