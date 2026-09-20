import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Task } from '../../models/task';
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

@Component({
  selector: 'app-task-details',
  imports: [RouterLink, FormsModule],
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

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef,
    private boardService: BoardService,
    private commentService : CommentService,
    private auth: Auth
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

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load task', error);

        this.errorMessage = 'Failed to load task.';
        this.isLoading = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  loadBoard(boardId: number): void {
    this.boardService.getById(boardId).subscribe({
      next: (board) => {
        this.board = board;

        this.loadParticipants(boardId);

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load board', error);
      }
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

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to assign task', error);

        this.assignErrorMessage = 'Failed to assign task.';

        this.changeDetectorRef.detectChanges();
      }
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
      }
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
      }
    });
  }

  addComment(): void {
    this.commentErrorMessage = '';

    if (!this.newCommentText.trim()) {
      this.commentErrorMessage = 'Comment cannot be empty.';
      return;
    }

    const comment: CreateComment = {
      text: this.newCommentText.trim(),
      taskId: this.taskId
    };

    this.commentService.create(comment).subscribe({
      next: () => {
        this.newCommentText = '';

        this.loadComments(this.taskId);
      },
      error: (error) => {
        console.error('Failed to create comment', error);

        this.commentErrorMessage = 'Failed to add comment.';
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  deleteComment(commentId: number): void {
    this.commentService.delete(commentId).subscribe({
      next: () => {
        this.loadComments(this.taskId);
      },
      error: (error) => {
        console.error('Failed to delete comment', error);

        this.commentErrorMessage = 'Failed to delete comment.';
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  startEditComment(comment: Comment): void {
    this.editingCommentId = comment.id;
    this.editingCommentText = comment.text;
  }

  saveComment(commentId: number): void {
    this.commentErrorMessage = '';

    if (!this.editingCommentText.trim()) {
      this.commentErrorMessage = 'Comment cannot be empty.';
      return;
    }

    const comment: UpdateComment = {
      text: this.editingCommentText.trim()
    };

    this.commentService.update(commentId, comment).subscribe({
      next: () => {
        this.editingCommentId = null;
        this.editingCommentText = '';

        this.loadComments(this.taskId);
      },
      error: (error) => {
        console.error('Failed to update comment', error);

        this.commentErrorMessage = 'Failed to update comment.';
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  isCurrentUser(userId: number): boolean {
    return this.auth.currentUser?.id === userId;
  }
}
