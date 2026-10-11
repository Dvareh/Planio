export type ActivityType =
  | 'TASK_CREATED'
  | 'STATUS_CHANGED'
  | 'PRIORITY_CHANGED'
  | 'DUE_DATE_CHANGED'
  | 'ASSIGNEE_CHANGED'
  | 'LABEL_ADDED'
  | 'LABEL_REMOVED'
  | 'COMMENT_ADDED'
  | 'COMMENT_UPDATED'
  | 'TITLE_CHANGED'
  | 'DESCRIPTION_CHANGED'
  | 'COMMENT_DELETED';

export interface TaskActivity {
  id: number;
  taskId: number;

  userId: number | null;
  username: string;

  type: ActivityType;

  oldValue: string | null;
  newValue: string | null;

  createdAt: string;
}
