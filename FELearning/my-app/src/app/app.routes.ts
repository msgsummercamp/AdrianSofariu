import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AboutComponent } from './features/about/about.component';
import { LoginComponent } from './features/login/login.component';
import { NotFoundComponent } from './features/not-found/not-found.component';
import { UserProfileComponent } from './features/userprofile/user-profile.component';
import { authenticatedGuard } from './core/auth/guards/authenticated.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'about', component: AboutComponent },
  {
    path: 'profile',
    loadComponent: () =>
      import('./features/userprofile/user-profile.component').then(
        (m) => m.UserProfileComponent,
      ),
    canActivate: [authenticatedGuard],
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then((m) => m.LoginComponent),
    canActivate: [authenticatedGuard],
  },
  { path: '**', component: NotFoundComponent },
];
