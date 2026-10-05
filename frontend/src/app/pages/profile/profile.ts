import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-profile',
  imports: [FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {
  username = '';
  email = '';
  role = '';

  oldPassword = '';
  newPassword = '';

  isLoading = true;
  isSaving = false;
  isChangingPassword = false;

  successMessage = '';
  errorMessage = '';
  passwordSuccessMessage = '';
  passwordErrorMessage = '';

  isEditing = false;

  constructor(
    private auth: Auth,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.auth.getCurrentUser().subscribe({
      next: (user) => {
        this.username = user.name;
        this.email = user.email;
        this.role = user.role;

        this.isLoading = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load profile:', error);

        this.isLoading = false;
        this.errorMessage = 'Failed to load profile.';

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  saveProfile(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (!this.username.trim() || !this.email.trim()) {
      this.errorMessage = 'Username and email are required.';
      return;
    }

    const emailPattern =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(this.email)) {
      this.errorMessage = 'Please enter a valid email address.';
      return;
    }

    this.isSaving = true;

    this.auth.updateCurrentUser({
      username: this.username,
      email: this.email
    }).subscribe({
      next: (user) => {
        this.username = user.username;
        this.email = user.email;

        this.isSaving = false;
        this.isEditing = false;

        this.successMessage = 'Profile updated successfully.';

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to update profile:', error);

        this.isSaving = false;

        if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Failed to update profile.';
        }

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  changePassword(): void {
    this.passwordSuccessMessage = '';
    this.passwordErrorMessage = '';

    if (!this.oldPassword || !this.newPassword) {
      this.passwordErrorMessage = 'Both password fields are required.';
      return;
    }

    this.isChangingPassword = true;

    this.auth.changePassword(
      this.oldPassword,
      this.newPassword
    ).subscribe({
      next: () => {
        this.oldPassword = '';
        this.newPassword = '';

        this.isChangingPassword = false;
        this.passwordSuccessMessage = 'Password changed successfully.';

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to change password:', error);

        this.isChangingPassword = false;

        if (error.error?.message) {
          this.passwordErrorMessage = error.error.message;
        } else {
          this.passwordErrorMessage = 'Failed to change password.';
        }

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  startEditing(): void {
    this.isEditing = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  cancelEditing(): void {
    this.isEditing = false;
    this.loadProfile();
  }
}
