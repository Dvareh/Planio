import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { Board as BoardModel} from '../../models/board'
import { BoardService } from '../../services/board';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Task } from '../../models/task';
import { TaskService } from '../../services/task';
import {CreateTaskModal} from '../../components/create-task-modal/create-task-modal';
import {EditTaskModal} from '../../components/edit-task-modal/edit-task-modal';
import {DeleteTaskModal} from '../../components/delete-task-modal/delete-task-modal';
import { TaskStatusLabelPipe } from '../../pipes/task-status-label-pipe';
import { TaskDeadlinePipe } from '../../pipes/task-deadline-pipe';
import { User } from '../../models/user';
import { Auth } from '../../services/auth';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-board',
  imports: [
    RouterLink,
    CreateTaskModal,
    EditTaskModal,
    DeleteTaskModal,
    TaskStatusLabelPipe,
    TaskDeadlinePipe,
    FormsModule,
  ],
  templateUrl: './board.html',
  styleUrl: './board.css',
})
export class Board implements OnInit {
  boardId!: number;
  board: BoardModel | null = null;
  tasks: Task[] = [];

  showCreateForm = false;

  showEditForm = false;
  selectedTaskId: number | null = null;

  showDeleteForm = false;
  selectedTaskTitle = '';

  participants: User[] = [];

  participantEmail = '';
  isAddingParticipant = false;
  participantMessage = '';
  participantError = '';

  constructor(
    private route: ActivatedRoute,
    private boardService: BoardService,
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef,
    private auth: Auth,
  ) {}

  ngOnInit(): void {
    this.boardId = Number(this.route.snapshot.paramMap.get('id'));
    this.auth.loadCurrentUser().subscribe();
    this.loadBoard();
    this.loadTasks();
    this.loadParticipants();
  }

  loadBoard(): void {
    this.boardService.getById(this.boardId).subscribe({
      next: (board) => {
        this.board = board;
      },
      error: (error) => {
        console.error('Failed to load board', error);
      },
    });
  }

  loadTasks(): void {
    this.taskService.getTasks(this.boardId).subscribe({
      next: (page) => {
        this.tasks = page.content;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load tasks', error);
      },
    });
  }

  onTaskCreated(): void {
    this.showCreateForm = false;
    this.loadTasks();
  }

  openEditTask(taskId: number): void {
    this.selectedTaskId = taskId;
    this.showEditForm = true;
  }

  onTaskUpdated(): void {
    this.showEditForm = false;
    this.selectedTaskId = null;
    this.loadTasks();
  }

  openDeleteTask(taskId: number, taskTitle: string): void {
    this.selectedTaskId = taskId;
    this.selectedTaskTitle = taskTitle;
    this.showDeleteForm = true;
  }

  onTaskDeleted(): void {
    this.showDeleteForm = false;
    this.selectedTaskId = null;
    this.selectedTaskTitle = '';

    this.loadTasks();
  }

  loadParticipants(): void {
    this.boardService.getParticipants(this.boardId).subscribe({
      next: (participants) => {
        this.participants = participants;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load participants', error);
      },
    });
  }

  isOwner(): boolean {
    return this.board?.ownerId === this.auth.currentUser?.id;
  }

  addParticipant(): void {
    this.participantMessage = '';
    this.participantError = '';

    if (!this.participantEmail.trim()) {
      this.participantError = 'Email is required.';
      return;
    }

    this.isAddingParticipant = true;

    this.boardService.addParticipant(this.boardId, this.participantEmail.trim()).subscribe({
      next: () => {
        this.participantEmail = '';
        this.isAddingParticipant = false;
        this.participantMessage = 'Member added successfully.';

        this.loadParticipants();
      },
      error: (error) => {
        console.error('Failed to add participant', error);

        this.isAddingParticipant = false;

        if (error.error?.message) {
          this.participantError = error.error.message;
        } else {
          this.participantError = 'Failed to add member.';
        }

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  removeParticipant(userId: number): void {
    this.participantMessage = '';
    this.participantError = '';

    this.boardService.removeParticipant(this.boardId, userId).subscribe({
      next: () => {
        this.participantMessage = 'Member removed successfully.';

        this.loadParticipants();
      },
      error: (error) => {
        console.error('Failed to remove participant', error);

        if (error.error?.message) {
          this.participantError = error.error.message;
        } else {
          this.participantError = 'Failed to remove member.';
        }

        this.changeDetectorRef.detectChanges();
      },
    });
  }
}
