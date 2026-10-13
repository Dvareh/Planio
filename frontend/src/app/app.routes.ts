import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Dashboard } from './pages/dashboard/dashboard';
import { Layout } from './layout/layout';
import { authGuard } from './guards/auth-guard';
import {guestGuard} from './guards/guest-guard';
import { CreateBoardPage } from './pages/create-board/create-board';
import { Board } from './pages/board/board';
import { TaskDetails } from './pages/task-details/task-details';
import { Notifications } from './pages/notifications/notifications';
import {Profile} from './pages/profile/profile';
import { BoardSettings } from './pages/board-settings/board-settings';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./pages/home/home').then(m => m.Home),
  },
  {
    path: 'login',
    component: Login,
    canActivate: [guestGuard],
  },
  {
    path: 'register',
    component: Register,
    canActivate: [guestGuard],
  },
  {
    path: '',
    component: Layout,
    canActivateChild: [authGuard],
    children: [
      { path: 'dashboard', component: Dashboard },
      {
        path: 'boards',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      { path: 'create-board', component: CreateBoardPage },
      { path: 'boards/:id/settings', component: BoardSettings },
      { path: 'boards/:id', component: Board },
      { path: 'tasks/:id', component: TaskDetails },
      { path: 'notifications', component: Notifications },
      { path: 'profile', component: Profile },
    ],
  },
  { path: '**', redirectTo: '' },
];
