import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Task, TaskPriority } from '../../models/task';
import { TaskService } from '../../services/task';
import { Board as BoardModel } from '../../models/board';
import { BoardService } from '../../services/board';
import { FormsModule } from '@angular/forms';
import { User } from '../../models/user';
import { Comment } from '../../models/comment';
import { CommentService } from '../../services/comment';
import { CreateComment } from '../../models/create-comment';
import { UpdateComment } from '../../models/update-comment';
import { Auth } from '../../services/auth';
import { TaskStatusLabelPipe } from '../../pipes/task-status-label-pipe';
import { TaskPriorityLabelPipe } from '../../pipes/task-priority-label-pipe';
import { Label } from '../../models/label';
import { LabelService } from '../../services/label';
import { TaskActivity } from '../../models/task-activity';
import { TaskStatus } from '../../models/task-status';
import { DatePipe } from '@angular/common';
import { UpdateTask } from '../../models/update-task';

@Component({
  selector: 'app-task-details',
  imports: [RouterLink, FormsModule, DatePipe],
  providers: [TaskStatusLabelPipe, TaskPriorityLabelPipe],
  templateUrl: './task-details.html',
  styleUrl: './task-details.css',
})
export class TaskDetails {
  taskId!: number;
  task: Task | null = null;

  errorMessage = '';
  isLoading = true;

  board: BoardModel | null = null;

  participants: User[] = [];

  selectedUserId: number | null = null;
  assignErrorMessage = '';

  comments: Comment[] = [];
  newCommentText = '';
  commentErrorMessage = '';

  editingCommentId: number | null = null;
  editingCommentText = '';

  availableLabels: Label[] = [];
  labelErrorMessage = '';

  activities: TaskActivity[] = [];
  activityErrorMessage = '';

  editingTask = false;
  editErrorMessage = '';

  editTitle = '';
  editDescription = '';
  editDueDate = '';

  isDeleting = false;
  deleteErrorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef,
    private boardService: BoardService,
    private commentService: CommentService,
    private auth: Auth,
    private labelService: LabelService,
    private taskStatusLabelPipe: TaskStatusLabelPipe,
    private taskPriorityLabelPipe: TaskPriorityLabelPipe,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.taskId = Number(this.route.snapshot.paramMap.get('id'));

    this.loadTask();
  }

  loadTask(): void {
    this.taskService.getById(this.taskId).subscribe({
      next: (task) => {
        this.task = task;
        this.isLoading = false;

        this.loadBoard(task.boardId);
        this.loadComments(task.id);
        this.loadActivity(task.id);

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load task', error);

        this.errorMessage = 'Failed to load task.';
        this.isLoading = false;

        if (error.status === 403) {
          this.router.navigate(['/dashboard']);
        }

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadBoard(boardId: number): void {
    this.boardService.getById(boardId).subscribe({
      next: (board) => {
        this.board = board;

        this.loadParticipants(boardId);
        this.loadLabels(boardId);

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load board', error);
      },
    });
  }

  assignTask(): void {
    this.assignErrorMessage = '';

    if (this.selectedUserId === null) {
      this.assignErrorMessage = 'Please select a user.';
      return;
    }

    this.taskService.assignTask(this.taskId, this.selectedUserId).subscribe({
      next: (task) => {
        this.task = task;

        this.loadActivity(this.taskId);

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to assign task', error);

        this.assignErrorMessage = 'Failed to assign task.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadParticipants(boardId: number): void {
    this.boardService.getParticipants(boardId).subscribe({
      next: (participants) => {
        this.participants = participants;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load participants', error);
      },
    });
  }

  loadComments(taskId: number): void {
    this.commentService.getByTask(taskId).subscribe({
      next: (comments) => {
        this.comments = comments;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load comments', error);
      },
    });
  }

  addComment(): void {
    this.commentErrorMessage = '';
    const text = this.newCommentText.trim();

    if (!text) {
      this.commentErrorMessage = 'Comment cannot be empty.';
      return;
    }

    const comment: CreateComment = {
      text,
      taskId: this.taskId,
    };

    this.commentService.create(comment).subscribe({
      next: (created) => {
        this.comments = [...this.comments, created];
        this.newCommentText = '';

        this.loadActivity(this.taskId);
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        this.commentErrorMessage = 'Failed to add comment.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  deleteComment(commentId: number): void {
    const confirmed = confirm('Are you sure you want to delete this comment?');

    if (!confirmed) {
      return;
    }

    this.commentErrorMessage = '';

    this.commentService.delete(commentId).subscribe({
      next: () => {
        this.comments = this.comments.filter(
          comment => comment.id !== commentId
        );

        this.loadActivity(this.taskId);
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to delete comment', error);

        this.commentErrorMessage = 'Failed to delete comment.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  startEditComment(comment: Comment): void {
    this.editingCommentId = comment.id;
    this.editingCommentText = comment.text;
  }

  saveComment(commentId: number): void {
    this.commentErrorMessage = '';

    const text = this.editingCommentText.trim();

    if (!text) {
      this.commentErrorMessage = 'Comment cannot be empty.';
      return;
    }

    const comment: UpdateComment = { text };

    this.commentService.update(commentId, comment).subscribe({
      next: (updated) => {
        this.comments = this.comments.map(item =>
          item.id === updated.id ? updated : item
        );

        this.editingCommentId = null;
        this.editingCommentText = '';

        this.loadActivity(this.taskId);
        this.changeDetectorRef.detectChanges();
      },
      error: () => {
        this.commentErrorMessage = 'Failed to update comment.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  isCurrentUser(userId: number): boolean {
    return this.auth.currentUser?.id === userId;
  }

  unassignTask(): void {
    this.assignErrorMessage = '';

    this.taskService.unassignTask(this.taskId).subscribe({
      next: (task) => {
        this.task = task;

        this.selectedUserId = null;

        this.loadActivity(this.taskId);

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to unassign task', error);

        this.assignErrorMessage = 'Failed to unassign task.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadLabels(boardId: number): void {
    this.labelService.getBoardLabels(boardId).subscribe({
      next: (labels) => {
        this.availableLabels = labels;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load labels', error);
      },
    });
  }

  hasLabel(labelId: number): boolean {
    if (!this.task) {
      return false;
    }
    for (const label of this.task.labels) {
      if (label.id === labelId) {
        return true;
      }
    }
    return false;
  }

  addLabel(labelId: number): void {
    this.labelErrorMessage = '';

    this.taskService.addLabel(this.taskId, labelId).subscribe({
      next: (task) => {
        this.task = task;
        this.loadActivity(this.taskId);
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to add label', error);

        this.labelErrorMessage = 'Failed to add label.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  removeLabel(labelId: number): void {
    this.labelErrorMessage = '';

    this.taskService.removeLabel(this.taskId, labelId).subscribe({
      next: (task) => {
        this.task = task;
        this.loadActivity(this.taskId);
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to remove label', error);

        this.labelErrorMessage = 'Failed to remove label.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  getAssignedUserName(): string {
    if (!this.task || this.task.assignedUserId === null) {
      return 'Not assigned';
    }

    for (const user of this.participants) {
      if (user.id === this.task.assignedUserId) {
        return user.username;
      }
    }

    return 'Unknown user';
  }

  loadActivity(taskId: number): void {
    this.taskService.getActivity(taskId).subscribe({
      next: (activities) => {
        this.activities = activities;
        this.activityErrorMessage = '';
        this.changeDetectorRef.detectChanges();
      },

      error: (error) => {
        console.error('Failed to load activity', error);
        this.activityErrorMessage = 'Failed to load activity history.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  getActivityText(activity: TaskActivity): string {
    switch (activity.type) {
      case 'TASK_CREATED':
        return 'created the task';

      case 'STATUS_CHANGED':
        return `changed status from "${this.taskStatusLabelPipe.transform(
          activity.oldValue as TaskStatus,
        )}" to "${this.taskStatusLabelPipe.transform(activity.newValue as TaskStatus)}"`;

      case 'PRIORITY_CHANGED':
        return `changed priority from ${this.taskPriorityLabelPipe.transform(
          activity.oldValue as TaskPriority,
        )} to ${this.taskPriorityLabelPipe.transform(activity.newValue as TaskPriority)}`;

      case 'DUE_DATE_CHANGED':
        return `changed due date from ${activity.oldValue} to ${activity.newValue}`;

      case 'ASSIGNEE_CHANGED':
        if (!activity.oldValue) {
          return `assigned task to ${activity.newValue}`;
        }

        if (!activity.newValue) {
          return `unassigned ${activity.oldValue}`;
        }

        return `changed assignee from ${activity.oldValue} to ${activity.newValue}`;

      case 'LABEL_ADDED':
        return `added label ${activity.newValue}`;

      case 'LABEL_REMOVED':
        return `removed label ${activity.oldValue}`;

      case 'COMMENT_ADDED':
        return 'added a comment';

      case 'COMMENT_UPDATED':
        return 'edited a comment';

      case 'COMMENT_DELETED':
        return 'deleted a comment';

      case 'TITLE_CHANGED':
        return `changed title from "${activity.oldValue}" to "${activity.newValue}"`;

      case 'DESCRIPTION_CHANGED':
        return 'changed the description';

      default:
        return 'updated the task';
    }
  }

  startEditingTask(): void {
    if (!this.task) {
      return;
    }

    this.editTitle = this.task.title;
    this.editDescription = this.task.description ?? '';
    this.editDueDate = this.task.dueDate ?? '';

    this.editingTask = true;
  }

  cancelEditingTask(): void {
    this.editingTask = false;
  }

  saveTask(): void {
    if (!this.task) {
      return;
    }

    this.editErrorMessage = '';

    const title = this.editTitle.trim();

    if (!title) {
      return;
    }

    const request: UpdateTask = {
      title,
      description: this.editDescription.trim(),
      dueDate: this.editDueDate || null,
    };

    this.taskService.update(this.task.id, request).subscribe({
      next: (updatedTask) => {
        this.task = updatedTask;
        this.editingTask = false;

        this.loadActivity(updatedTask.id);
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {

        this.editErrorMessage = 'Failed to update task.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  changeStatus(): void {
    if (!this.task) {
      return;
    }

    const status = this.task.status;

    this.taskService.updateStatus(this.task.id, status).subscribe({
      next: (updatedTask) => {
        this.task = updatedTask;

        this.loadActivity(this.taskId);

        this.changeDetectorRef.detectChanges();
      },

      error: (error) => {
        console.error('Failed to update status', error);
        this.loadTask();
      },
    });
  }

  changePriority(): void {
    if (!this.task) {
      return;
    }

    const priority = this.task.priority;

    this.taskService.updatePriority(this.task.id, priority).subscribe({
      next: (updatedTask) => {
        this.task = updatedTask;

        this.loadActivity(this.taskId);

        this.changeDetectorRef.detectChanges();
      },

      error: (error) => {
        console.error('Failed to update priority', error);

        this.loadTask();
      },
    });
  }

  deleteTask(): void {
    if (!this.task || this.isDeleting) {
      return;
    }

    const task = this.task;

    const confirmed = window.confirm(`Delete task "${task.title}"? This action cannot be undone.`);

    if (!confirmed) {
      return;
    }

    this.deleteErrorMessage = '';
    this.isDeleting = true;

    this.taskService.delete(task.id).subscribe({
      next: () => {
        this.router.navigate(['/boards', task.boardId]);
      },

      error: (error) => {
        console.error('Failed to delete task', error);

        this.isDeleting = false;
        this.deleteErrorMessage = 'Failed to delete task. Please try again.';

        this.changeDetectorRef.detectChanges();
      }
    });
  }
}
