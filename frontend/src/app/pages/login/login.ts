import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  email = '';
  password = '';
  errorMessage = '';

  onSubmit(): void {
    this.errorMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Please fill in all fields';
      return;
    }
    if (!this.email.includes('@')) {
      this.errorMessage = 'Please enter a valid email';
      return;
    }
    this.auth.login({
      email: this.email,
      password: this.password
    }).subscribe({
      next: () => {
        this.auth.getCurrentUser().subscribe({
          next: () => {
            this.router.navigate(['/dashboard']);
          },
          error: () => {
            this.errorMessage = 'Failed to load user data';
          }
        });
      },
      error: () => {
        this.errorMessage = 'Invalid email or password';
        this.cdr.detectChanges();
      }
    });
  }
}
