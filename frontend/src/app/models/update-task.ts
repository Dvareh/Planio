import { TaskStatus } from './task-status';

export interface UpdateTask {
  title: string;
  description: string;
  dueDate: string;
  status: TaskStatus;
}
