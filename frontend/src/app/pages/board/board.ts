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


@Component({
  selector: 'app-board',
  imports: [
    RouterLink,
    CreateTaskModal,
    EditTaskModal,
    DeleteTaskModal,
    TaskStatusLabelPipe,
    TaskDeadlinePipe,
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

  constructor(
    private route: ActivatedRoute,
    private boardService: BoardService,
    private taskService: TaskService,
    private changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.boardId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadBoard();
    this.loadTasks();
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
}
