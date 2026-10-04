import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Notification as NotificationModel } from '../models/notification';


@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly apiUrl =
    `${environment.apiUrl}/api/notifications`;

  private readonly notificationsChanged =
    new Subject<void>();

  constructor(private http: HttpClient) {}

  getNotifications(): Observable<NotificationModel[]> {
    return this.http.get<NotificationModel[]>(this.apiUrl);
  }

  getUnreadNotifications(): Observable<NotificationModel[]> {
    return this.http.get<NotificationModel[]>(
      `${this.apiUrl}/unread`
    );
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(
      `${this.apiUrl}/${id}/read`,
      {}
    );
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<void>(
      `${this.apiUrl}/read-all`,
      {}
    );
  }

  notifyNotificationsChanged(): void {
    this.notificationsChanged.next();
  }

  getNotificationsChanged(): Observable<void> {
    return this.notificationsChanged.asObservable();
  }
}
