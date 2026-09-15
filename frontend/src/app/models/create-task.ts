import { TaskStatus } from './task-status';

export interface CreateTask {
  title: string;
  description: string;
  dueDate: string;
  status?: TaskStatus;
  boardId: number;
  assignedUserId?: number;
}
