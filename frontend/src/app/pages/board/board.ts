import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { Board as BoardModel} from '../../models/board'
import { BoardService } from '../../services/board';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Task } from '../../models/task';
import { TaskService } from '../../services/task';
import { CreateTaskModal } from '../../components/create-task-modal/create-task-modal';
import { EditTaskModal } from '../../components/edit-task-modal/edit-task-modal';
import { DeleteTaskModal } from '../../components/delete-task-modal/delete-task-modal';
import { TaskStatusLabelPipe } from '../../pipes/task-status-label-pipe';
import { TaskDeadlinePipe } from '../../pipes/task-deadline-pipe';
import { User } from '../../models/user';
import { Auth } from '../../services/auth';
import { FormsModule } from '@angular/forms';
import { TaskPriorityLabelPipe } from '../../pipes/task-priority-label-pipe';
import { TaskStatus } from '../../models/task-status';
import { Label } from '../../models/label';
import { LabelService } from '../../services/label';
import { TaskPriority } from '../../models/task';
import { CdkDrag, CdkDragDrop, CdkDropList, CdkDropListGroup, transferArrayItem } from '@angular/cdk/drag-drop';


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
    TaskPriorityLabelPipe,
    CdkDropListGroup,
    CdkDropList,
    CdkDrag
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

  page = 0;
  size = 10;

  sortBy = 'dueDate';
  direction = 'asc';

  totalPages = 0;

  search = '';

  selectedStatus: TaskStatus | '' = '';
  selectedPriority: TaskPriority | '' = '';

  selectedAssignedUserId: number | null = null;
  selectedLabelId: number | null = null;

  dueDateFrom = '';
  dueDateTo = '';

  labels: Label[] = [];

  viewMode: 'list' | 'kanban' = 'list';

  todoTasks: Task[] = [];
  inProgressTasks: Task[] = [];
  doneTasks: Task[] = [];
  cancelledTasks: Task[] = [];

  constructor(
    private route: ActivatedRoute,
    private boardService: BoardService,
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef,
    private auth: Auth,
    private labelService: LabelService,
  ) {}

  ngOnInit(): void {
    this.boardId = Number(this.route.snapshot.paramMap.get('id'));
    this.auth.loadCurrentUser().subscribe();
    this.loadBoard();
    this.loadLabels();
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
    this.taskService.getTasks({

      boardId: this.boardId,

      search: this.search.trim() ? this.search.trim() : undefined,

      status: this.selectedStatus ? this.selectedStatus : undefined,

      priority: this.selectedPriority ? this.selectedPriority : undefined,

      assignedUserId: this.selectedAssignedUserId !== null ? this.selectedAssignedUserId : undefined,

      labelId: this.selectedLabelId !== null ? this.selectedLabelId : undefined,

      dueDateFrom: this.dueDateFrom ? this.dueDateFrom : undefined,

      dueDateTo: this.dueDateTo ? this.dueDateTo : undefined,

      page: this.viewMode === 'kanban'
        ? 0
        : this.page,

      size: this.viewMode === 'kanban'
        ? 1000
        : this.size,

      sortBy: this.sortBy,

      direction: this.direction

    }).subscribe({

      next: (response) => {

        this.tasks = response.content;

        this.buildKanbanColumns();

        this.totalPages = response.totalPages;

        this.changeDetectorRef.detectChanges();
      },

      error: (error) => {

        console.error(
          'Failed to load tasks',
          error
        );
      }
    });
  }

  loadLabels(): void {

    this.labelService
      .getBoardLabels(this.boardId)
      .subscribe({
        next: (labels) => {
          this.labels = labels;

          this.changeDetectorRef.detectChanges();
        },
        error: (error) => {
          console.error('Failed to load labels', error);
        }
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

  applyFilters(): void {

    this.page = 0;

    this.loadTasks();
  }

  clearFilters(): void {

    this.search = '';

    this.selectedStatus = '';

    this.selectedPriority = '';

    this.selectedAssignedUserId = null;

    this.selectedLabelId = null;

    this.dueDateFrom = '';

    this.dueDateTo = '';

    this.page = 0;

    this.loadTasks();
  }

  changeView(mode: 'list' | 'kanban'): void {

    if (this.viewMode === mode) {
      return;
    }

    this.viewMode = mode;
    this.page = 0;

    this.loadTasks();
  }

  buildKanbanColumns(): void {

    this.todoTasks = [];
    this.inProgressTasks = [];
    this.doneTasks = [];
    this.cancelledTasks = [];

    for (const task of this.tasks) {

      switch (task.status) {

        case 'TODO':
          this.todoTasks.push(task);
          break;

        case 'IN_PROGRESS':
          this.inProgressTasks.push(task);
          break;

        case 'DONE':
          this.doneTasks.push(task);
          break;

        case 'CANCELLED':
          this.cancelledTasks.push(task);
          break;
      }
    }
  }

  dropTask(event: CdkDragDrop<Task[]>, newStatus: TaskStatus): void {
    if (event.previousContainer === event.container) {
      return;
    }

    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex
    );

    const task = event.container.data[event.currentIndex];

    const oldStatus = task.status;

    task.status = newStatus;

    this.taskService.updateStatus(task.id, newStatus).subscribe({
      next: (updatedTask) => {
        task.status = updatedTask.status;
        this.changeDetectorRef.detectChanges();
      },

      error: (error) => {
        console.error('Failed to update task status', error);
        task.status = oldStatus;
        this.loadTasks();
      }
    });
  }
}
