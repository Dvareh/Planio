import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NotificationService } from '../../services/notification';
import { Notification as NotificationModel } from '../../models/notification';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-notifications',
  imports: [DatePipe, RouterLink],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css',
})
export class Notifications implements OnInit {
  notifications: NotificationModel[] = [];

  isLoading = true;
  errorMessage = '';

  constructor(
    private notificationService: NotificationService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.notificationService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.isLoading = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load notifications:', error);

        this.isLoading = false;
        this.errorMessage = 'Failed to load notifications.';

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  markAsRead(notification: NotificationModel): void {
    if (notification.read) {
      return;
    }

    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        notification.read = true;

        this.notificationService.notifyNotificationsChanged();

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to mark notification as read:', error);
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        for (const notification of this.notifications) {
          notification.read = true;
        }

        this.notificationService.notifyNotificationsChanged();

        this.changeDetectorRef.detectChanges();

      },
      error: (error) => {
        console.error('Failed to mark all notifications as read:', error);
      }
    });
  }

  get unreadCount(): number {
    return this.notifications.filter(
      notification => !notification.read
    ).length;
  }

  getNotificationText(notification: NotificationModel): string {
    if (notification.type === '3_DAYS_BEFORE_DEADLINE') {
      return `Task "${notification.taskTitle}" is due in 3 days.`;
    }

    if (notification.type === '1_DAYS_BEFORE_DEADLINE') {
      return `Task "${notification.taskTitle}" is due tomorrow.`;
    }

    return `Notification for task "${notification.taskTitle}".`;
  }
}
