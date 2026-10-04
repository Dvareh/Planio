export interface Notification {
  id: number;
  taskId: number;
  taskTitle: string;
  type: string;
  sentAt: string;
  read: boolean;
}
