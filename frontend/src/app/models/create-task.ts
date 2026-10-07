import { TaskStatus } from './task-status';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface CreateTask {
  title: string;
  description: string;
  dueDate: string;
  status?: TaskStatus;
  boardId: number;
  assignedUserId?: number;
  priority: TaskPriority;
}
