import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'dashboard/workspace/:id',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/workspace-detail/workspace-detail.component').then(m => m.WorkspaceDetailComponent)
  },
  {
    path: 'dashboard/workspace/:id/endpoints/new',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/create-endpoint/create-endpoint.component').then(m => m.CreateEndpointComponent)
  },
  {
    path: 'dashboard/workspace/:id/logs',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/request-logs/request-logs.component').then(m => m.RequestLogsComponent)
  },
  {
    path: 'settings',
    canActivate: [authGuard],
    loadComponent: () => import('./features/settings/settings.component').then(m => m.SettingsComponent)
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
