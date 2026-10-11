import { Pipe, PipeTransform } from '@angular/core';
import { TaskStatus } from '../models/task-status';

@Pipe({
  name: 'taskDeadline',
  standalone: true
})
export class TaskDeadlinePipe implements PipeTransform {

  transform(dueDate: string | null, status: TaskStatus): string {
    if (!dueDate || status === 'DONE' || status === 'CANCELLED') {
      return '';
    }

    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const today = `${year}-${month}-${day}`;

    return dueDate < today ? 'Overdue' : '';
  }
}
