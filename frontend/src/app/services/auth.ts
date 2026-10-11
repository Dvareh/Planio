import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, of, tap } from 'rxjs';
import { CurrentUser } from '../models/current-user';
import { User } from '../models/user';

import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private readonly apiUrl = `${environment.apiUrl}/api/auth`;
  currentUser: CurrentUser | null = null;

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.currentUser = null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUser(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(`${environment.apiUrl}/api/users/me`).pipe(
      tap(user => {
        this.currentUser = user;
      })
    );
  }

  loadCurrentUser(): Observable<CurrentUser | null> {
    if (!this.getToken()) {
      return of(null);
    }

    return this.getCurrentUser().pipe(
      catchError(() => {
        this.logout();
        return of(null);
      })
    );
  }

  updateCurrentUser(request: { username: string; email: string; }): Observable<User> {
    return this.http.put<User>(`${environment.apiUrl}/api/users/me`, request).pipe(
      tap(user => {
        if (this.currentUser) {
          this.currentUser.name = user.username;
          this.currentUser.email = user.email;
        }
      })
    );
  }

  changePassword(oldPassword: string, newPassword: string): Observable<string> {
    return this.http.put(`${environment.apiUrl}/api/users/change-password`,
      { oldPassword, newPassword },
      { responseType: 'text' });
  }
}
