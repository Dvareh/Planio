import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'taskStatusLabel',
  standalone: true
})
export class TaskStatusLabelPipe implements PipeTransform {

  transform(status: string): string {
    if (status === 'TODO') {
      return 'To Do';
    }

    if (status === 'IN_PROGRESS') {
      return 'In Progress';
    }

    if (status === 'DONE') {
      return 'Done';
    }

    if (status === 'CANCELLED') {
      return 'Cancelled';
    }

    return status;
  }
}
