import { TaskStatus } from './task-status';
import { TaskPriority } from './task';

export interface UpdateTask {
  title: string;
  description: string;
  dueDate: string;
  status: TaskStatus;
  priority: TaskPriority;
}
