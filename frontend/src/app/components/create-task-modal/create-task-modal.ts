import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TaskStatus } from '../../models/task-status';
import { TaskService } from '../../services/task';
import { CreateTask } from '../../models/create-task';

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
    status: 'TODO' as TaskStatus
  };

  errorMsg = '';

  constructor(private taskService: TaskService,
              private changeDetectorRef: ChangeDetectorRef,) {}

  createTask() {
    this.errorMsg = '';

    if (!this.task.title.trim()) {
      this.errorMsg = 'Title is required.';
      return;
    }

    if (!this.task.dueDate) {
      this.errorMsg = 'Due date is required.';
      return;
    }

    const newTask: CreateTask = {
      title: this.task.title,
      description: this.task.description,
      dueDate: this.task.dueDate,
      status: this.task.status,
      boardId: this.boardId
    }

    this.taskService.create(newTask).subscribe({
      next: () => {
        this.taskCreated.emit();
      },
      error: (error) => {
        console.error('Failed to create task', error);
        this.errorMsg = 'Failed to create task.';
        this.changeDetectorRef.detectChanges()
      }
    });
  }

  closeModal(): void {
    this.close.emit();
  }
}
