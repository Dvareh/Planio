import { Pipe, PipeTransform } from '@angular/core';
import { TaskStatus } from '../models/task-status';

@Pipe({
  name: 'taskDeadline',
  standalone: true
})
export class TaskDeadlinePipe implements PipeTransform {

  transform(dueDate: string, status: TaskStatus): string {
    if (status === 'DONE' || status === 'CANCELLED') {
      return '';
    }

    if (!dueDate) {
      return '';
    }

    const dueDateTime = new Date(dueDate).getTime();
    const currentTime = new Date().getTime();

    if (dueDateTime < currentTime) {
      return 'Overdue';
    }

    return '';
  }
}
