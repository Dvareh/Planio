import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Task } from '../../models/task';
import { TaskService } from '../../services/task';
import { Board as BoardModel } from '../../models/board';
import { BoardService } from '../../services/board';
import { FormsModule } from '@angular/forms';
import { User } from '../../models/user';

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

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef,
    private boardService: BoardService
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
}
