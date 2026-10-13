import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TaskStatus } from '../../models/task-status';
import { TaskService } from '../../services/task';
import { CreateTask } from '../../models/create-task';
import { TaskPriority } from '../../models/task';

@Component({
  selector: 'app-create-task-modal',
  imports: [FormsModule],
  templateUrl: './create-task-modal.html',
  styleUrl: './create-task-modal.css',
})
export class CreateTaskModal {

  @Input() boardId!: number;

  @Output() close = new EventEmitter<void>();
  @Output() taskCreated = new EventEmitter<void>();

  task = {
    title: '',
    description: '',
    dueDate: '',
    status: 'TODO' as TaskStatus,
    priority: 'MEDIUM' as TaskPriority,
  };

  errorMsg = '';

  constructor(
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef,
  ) {}

  createTask(): void {
    this.errorMsg = '';

    if (!this.task.title.trim()) {
      this.errorMsg = 'Title is required.';
      return;
    }

    if (!this.task.dueDate) {
      this.errorMsg = 'Due date is required.';
      return;
    }

    if (this.task.dueDate < this.today) {
      this.errorMsg = 'Due date cannot be in the past.';
      return;
    }

    const newTask: CreateTask = {
      title: this.task.title,
      description: this.task.description,
      dueDate: this.task.dueDate,
      status: this.task.status,
      priority: this.task.priority,
      boardId: this.boardId,
    };

    this.taskService.create(newTask).subscribe({
      next: () => {
        this.taskCreated.emit();
      },
      error: (error) => {
        console.error('Failed to create task', error);

        const details = error.error;

        this.errorMsg =
          details?.dueDate ||
          details?.title ||
          details?.description ||
          details?.boardId ||
          details?.message ||
          'Failed to create task.';

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  closeModal(): void {
    this.close.emit();
  }

  get today(): string {
    const parts = new Intl.DateTimeFormat('en', {
      timeZone: 'Europe/Warsaw',
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    }).formatToParts(new Date());

    const year = parts.find(part => part.type === 'year')!.value;
    const month = parts.find(part => part.type === 'month')!.value;
    const day = parts.find(part => part.type === 'day')!.value;

    return `${year}-${month}-${day}`;
  }
}
