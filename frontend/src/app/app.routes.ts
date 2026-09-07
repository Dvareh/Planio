import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Dashboard } from './pages/dashboard/dashboard';
import { Layout } from './layout/layout';
import { authGuard } from './guards/auth-guard';
import {guestGuard} from './guards/guest-guard';
import { CreateBoardPage } from './pages/create-board/create-board';
import { EditBoard } from './pages/edit-board/edit-board';
import { Board } from './pages/board/board';

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
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: Dashboard,
        canActivate: [authGuard]
      },
      {
        path: 'create-board',
        component: CreateBoardPage,
        canActivate: [authGuard]
      },
      {
        path: 'edit-board/:id',
        component: EditBoard,
        canActivate: [authGuard]
      },
      {
        path: 'boards/:id',
        component: Board,
        canActivate: [authGuard]
      }
    ]
  }
];
