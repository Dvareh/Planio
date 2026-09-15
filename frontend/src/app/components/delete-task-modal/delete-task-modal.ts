import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task';

@Component({
  selector: 'app-delete-task-modal',
  imports: [FormsModule],
  templateUrl: './delete-task-modal.html',
  styleUrl: './delete-task-modal.css',
})
export class DeleteTaskModal {
  @Input() taskId!: number;
  @Input() taskTitle = '';

  @Output() close = new EventEmitter<void>();
  @Output() taskDeleted = new EventEmitter<void>();

  errorMessage = '';
  isDeleting = false;

  constructor(
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  deleteTask(): void {
    this.errorMessage = '';
    this.isDeleting = true;

    this.taskService.delete(this.taskId).subscribe({
      next: () => {
        this.taskDeleted.emit();
      },
      error: (error) => {
        console.error('Failed to delete task', error);

        this.errorMessage = 'Failed to delete task.';
        this.isDeleting = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  closeModal(): void {
    if (this.isDeleting) {
      return;
    }

    this.close.emit();
  }
}
