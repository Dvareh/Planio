import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';

import { Board as BoardModel} from '../../models/board'
import { BoardService } from '../../services/board';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Task } from '../../models/task';
import { TaskService } from '../../services/task';
import { CreateTaskModal } from '../../components/create-task-modal/create-task-modal';
import { User } from '../../models/user';
import { Auth } from '../../services/auth';
import { FormsModule } from '@angular/forms';
import { TaskPriorityLabelPipe } from '../../pipes/task-priority-label-pipe';
import { TaskStatus } from '../../models/task-status';
import { Label } from '../../models/label';
import { LabelService } from '../../services/label';
import { TaskPriority } from '../../models/task';
import { CdkDrag, CdkDragDrop, CdkDropList, CdkDropListGroup, transferArrayItem } from '@angular/cdk/drag-drop';
import {TaskFilters} from '../../models/task-filters';
import {Subscription} from 'rxjs';


@Component({
  selector: 'app-board',
  imports: [
    RouterLink,
    CreateTaskModal,
    FormsModule,
    TaskPriorityLabelPipe,
    CdkDropListGroup,
    CdkDropList,
    CdkDrag
  ],
  templateUrl: './board.html',
  styleUrl: './board.css',
})
export class Board implements OnInit, OnDestroy {
  boardId!: number;
  board: BoardModel | null = null;
  tasks: Task[] = [];

  showCreateForm = false;

  participants: User[] = [];

  sortBy = 'dueDate';
  direction = 'asc';


  search = '';

  selectedStatus: TaskStatus | '' = '';
  selectedPriority: TaskPriority | '' = '';

  selectedAssignedUserId: number | null = null;
  selectedLabelId: number | null = null;

  dueDateFrom = '';
  dueDateTo = '';

  labels: Label[] = [];

  readonly pageSize = 50;

  nextPage = 0;
  totalPages = 0;
  totalElements = 0;
  isLoadingTasks = false;
  taskLoadError = '';

  showLeaveConfirm = false;
  isLeaving = false;
  leaveError = '';

  @ViewChild('leaveButton') private leaveButton?: ElementRef<HTMLButtonElement>;

  private activeFilters: TaskFilters | null = null;
  private taskRequest?: Subscription;

  columns: { status: TaskStatus; title: string; tasks: Task[] }[] = [
    { status: 'TODO', title: 'To Do', tasks: [] },
    { status: 'IN_PROGRESS', title: 'In Progress', tasks: [] },
    { status: 'DONE', title: 'Done', tasks: [] },
    { status: 'CANCELLED', title: 'Cancelled', tasks: [] },
  ];

  constructor(
    private route: ActivatedRoute,
    private boardService: BoardService,
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef,
    private auth: Auth,
    private labelService: LabelService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.boardId = Number(this.route.snapshot.paramMap.get('id'));

    this.loadBoard();
  }

  loadBoard(): void {
    this.boardService.getById(this.boardId).subscribe({
      next: (board) => {
        this.board = board;

        this.loadLabels();
        this.loadTasks();
        this.loadParticipants();

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load board', error);

        if (error.status === 403 || error.status === 404) {
          this.router.navigate(['/dashboard']);
        }
      },
    });
  }

  loadTasks(): void {
    this.taskRequest?.unsubscribe();

    this.tasks = [];
    this.nextPage = 0;
    this.totalPages = 0;
    this.totalElements = 0;
    this.isLoadingTasks = false;
    this.taskLoadError = '';

    this.activeFilters = {
      boardId: this.boardId,
      search: this.search.trim() || undefined,
      status: this.selectedStatus || undefined,
      priority: this.selectedPriority || undefined,
      assignedUserId: this.selectedAssignedUserId ?? undefined,
      labelId: this.selectedLabelId ?? undefined,
      dueDateFrom: this.dueDateFrom || undefined,
      dueDateTo: this.dueDateTo || undefined,
      page: 0,
      size: this.pageSize,
      sortBy: this.sortBy,
      direction: this.direction,
    };

    this.buildKanbanColumns();
    this.loadMoreTasks();
  }

  loadMoreTasks(): void {
    if (!this.activeFilters || this.isLoadingTasks) {
      return;
    }

    if (this.nextPage > 0 && this.nextPage >= this.totalPages) {
      return;
    }

    this.isLoadingTasks = true;
    this.taskLoadError = '';

    this.taskRequest = this.taskService.getTasks({
      ...this.activeFilters,
      page: this.nextPage,
    }).subscribe({
      next: (response) => {
        this.tasks = [...this.tasks, ...response.content];
        this.nextPage = response.number + 1;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.isLoadingTasks = false;

        this.buildKanbanColumns();
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load tasks', error);

        this.taskLoadError = 'Failed to load tasks. Please try again.';
        this.isLoadingTasks = false;
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  ngOnDestroy(): void {
    this.taskRequest?.unsubscribe();
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

  canLeave(): boolean {
    const userId = this.auth.currentUser?.id;

    return !!this.board && userId !== undefined
      && this.board.ownerId !== userId
      && this.participants.some(user => user.id === userId);
  }

  cancelLeave(): void {
    if (this.isLeaving) {
      return;
    }

    this.showLeaveConfirm = false;
    this.leaveError = '';
    this.leaveButton?.nativeElement.focus();
  }

  leaveBoard(): void {
    if (!this.canLeave() || !this.showLeaveConfirm || this.isLeaving) {
      return;
    }

    this.isLeaving = true;
    this.leaveError = '';

    this.boardService.leaveBoard(this.boardId).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isLeaving = false;
        this.leaveError = error.error?.message || 'Failed to leave the board. Please try again.';
        this.changeDetectorRef.detectChanges();
      },
    });
  }

  applyFilters(): void {

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

    this.loadTasks();
  }


  buildKanbanColumns(): void {
    for (const column of this.columns) {
      column.tasks = this.tasks.filter(task => task.status === column.status);
    }
  }

  dropTask(event: CdkDragDrop<Task[]>, newStatus: TaskStatus): void {
    if (
      event.previousContainer === event.container ||
      this.isLoadingTasks
    ) {
      return;
    }

    const task = event.previousContainer.data[event.previousIndex];
    const oldStatus = task.status;

    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex
    );

    task.status = newStatus;

    this.taskService.updateStatus(task.id, newStatus).subscribe({
      next: () => {
        this.loadTasks();
      },
      error: (error) => {
        task.status = oldStatus;
        this.loadTasks();
      },
    });
  }
}
