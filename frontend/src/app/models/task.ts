import {TaskStatus} from './task-status';
import { Label } from './label';

export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface Task {
  id: number;
  title: string;
  description: string;
  dueDate: string;
  status: TaskStatus;
  boardId: number;
  assignedUserId: number | null;
  priority: TaskPriority;
  labels: Label[];
  taskKey: string;
}
