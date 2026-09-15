import {TaskStatus} from './task-status';

export interface Task {
  id: number;
  title: string;
  description: string;
  dueDate: string;
  status: TaskStatus;
  boardId: number;
  assignedUserId: number | null;
}
