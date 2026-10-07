import { TaskPriority } from './task';
import { TaskStatus } from './task-status';

export interface TaskFilters {
  boardId: number;

  search?: string;
  status?: TaskStatus;
  priority?: TaskPriority;

  assignedUserId?: number;
  labelId?: number;

  dueDateFrom?: string;
  dueDateTo?: string;

  page: number;
  size: number;

  sortBy: string;
  direction: string;
}
