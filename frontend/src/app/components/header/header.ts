import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';
import { NotificationService } from '../../services/notification';


@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit{
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);
  private readonly notificationService = inject(NotificationService);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  unreadNotifications = 0;

  ngOnInit(): void {
    this.loadUnreadNotifications();

    this.notificationService
      .getNotificationsChanged()
      .subscribe(() => {
        this.loadUnreadNotifications();
      });
  }

  loadUnreadNotifications(): void {
    this.notificationService.getUnreadNotifications().subscribe({
      next: (notifications) => {
        this.unreadNotifications = notifications.length;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load unread notifications:', error);
      }
    });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  get currentUser() {
    return this.auth.currentUser;
  }

  openNotifications(): void {
    this.router.navigate(['/notifications']);
  }
}
