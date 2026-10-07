import { Pipe, PipeTransform } from '@angular/core';
import { TaskPriority } from '../models/task';

@Pipe({
  name: 'taskPriorityLabel',
})
export class TaskPriorityLabelPipe implements PipeTransform {
  transform(priority: TaskPriority): string {

    switch (priority) {
      case 'LOW':
        return 'Low';

      case 'MEDIUM':
        return 'Medium';

      case 'HIGH':
        return 'High';

      case 'URGENT':
        return 'Urgent';

      default:
        return priority;
    }
  }
}
