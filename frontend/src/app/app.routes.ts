import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Dashboard } from './pages/dashboard/dashboard';
import { Layout } from './layout/layout';
import { authGuard } from './guards/auth-guard';
import {guestGuard} from './guards/guest-guard';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
    canActivate: [guestGuard]
  },
  {
    path: 'register',
    component: Register
  },
  {
    path: '',
    component: Layout,
    children: [
      {
        path: 'dashboard',
        component: Dashboard,
        canActivate: [authGuard]
      }
    ]
  }
];
