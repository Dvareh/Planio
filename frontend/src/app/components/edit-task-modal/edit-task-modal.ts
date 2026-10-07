import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { TaskStatus } from '../../models/task-status';
import { TaskService } from '../../services/task';
import { UpdateTask } from '../../models/update-task';
import { FormsModule } from '@angular/forms';
import { TaskPriority } from '../../models/task';

@Component({
  selector: 'app-edit-task-modal',
  imports: [FormsModule],
  templateUrl: './edit-task-modal.html',
  styleUrl: './edit-task-modal.css',
})
export class EditTaskModal {
  @Input() taskId!: number;

  @Output() close = new EventEmitter<void>();
  @Output() taskUpdated = new EventEmitter<void>();

  task = {
    title: '',
    description: '',
    dueDate: '',
    status: 'TODO' as TaskStatus,
    priority: 'MEDIUM' as TaskPriority,
  };

  errorMessage = '';
  isLoading = true;

  constructor(
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadTask();
  }

  loadTask(): void {
    this.taskService.getById(this.taskId).subscribe({
      next: (task) => {
        this.task.title = task.title;
        this.task.description = task.description;
        this.task.dueDate = task.dueDate;
        this.task.status = task.status;

        this.isLoading = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load task', error);

        this.errorMessage = 'Failed to load task.';
        this.isLoading = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  updateTask(): void {
    this.errorMessage = '';

    if (!this.task.title.trim()) {
      this.errorMessage = 'Title is required.';
      return;
    }

    if (!this.task.dueDate) {
      this.errorMessage = 'Due date is required.';
      return;
    }

    const updatedTask: UpdateTask = {
      title: this.task.title,
      description: this.task.description,
      dueDate: this.task.dueDate,
      status: this.task.status,
      priority: this.task.priority,
    };

    this.taskService.update(this.taskId, updatedTask).subscribe({
      next: () => {
        this.taskUpdated.emit();
      },
      error: (error) => {
        console.error('Failed to update task', error);

        this.errorMessage = 'Failed to update task.';

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  closeModal(): void {
    this.close.emit();
  }
}
