import { TaskStatus } from './task-status';
import { TaskPriority } from './task';

export interface CreateTask {
  title: string;
  description: string;
  dueDate: string;
  status?: TaskStatus;
  boardId: number;
  assignedUserId?: number;
  priority: TaskPriority;
}
