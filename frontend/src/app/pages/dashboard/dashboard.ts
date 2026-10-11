import { Component, OnInit } from '@angular/core';
import { BoardService } from '../../services/board';
import { Board as BoardModel } from '../../models/board';
import { ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import {Task} from '../../models/task';
import {TaskService} from '../../services/task';
import {DatePipe} from '@angular/common';
import { TaskStatusLabelPipe } from '../../pipes/task-status-label-pipe';
import { TaskDeadlinePipe } from '../../pipes/task-deadline-pipe';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, DatePipe, TaskStatusLabelPipe, TaskDeadlinePipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  boards: BoardModel[] = [];
  tasks: Task[] = [];

  todoCount = 0;
  inProgressCount = 0;
  doneCount = 0;
  cancelledCount = 0;

  boardsLoading = false;
  tasksLoading = false;

  boardsError = '';
  tasksError = '';

  constructor(
    private boardService: BoardService,
    private changeDetectorRef: ChangeDetectorRef,
    private taskService: TaskService,
  ) {}

  ngOnInit(): void {
    this.loadBoards();
    this.loadTasks();
  }

  loadBoards(): void {
    this.boardsLoading = true;
    this.boardsError = '';

    this.boardService.getMyBoards().subscribe({
      next: (boards) => {
        this.boards = boards;
        this.boardsLoading = false;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load boards:', error);
        this.boardsError = 'Failed to load boards.';
        this.boardsLoading = false;
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  loadTasks(): void {
    this.tasksLoading = true;
    this.tasksError = '';

    this.taskService.getMyTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.calculateStatistics();
        this.tasksLoading = false;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load tasks:', error);
        this.tasksError = 'Failed to load tasks.';
        this.tasksLoading = false;
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  calculateStatistics(): void {
    this.todoCount = 0;
    this.inProgressCount = 0;
    this.doneCount = 0;
    this.cancelledCount = 0;

    for (const task of this.tasks) {
      if (task.status === 'TODO') {
        this.todoCount++;
      }

      if (task.status === 'IN_PROGRESS') {
        this.inProgressCount++;
      }

      if (task.status === 'DONE') {
        this.doneCount++;
      }

      if (task.status === 'CANCELLED') {
        this.cancelledCount++;
      }
    }
  }

  private getTodayDate(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  get upcomingTasks(): Task[] {
    const today = this.getTodayDate();

    return this.tasks
      .filter(task =>
        task.dueDate !== null &&
        task.dueDate >= today &&
        task.status !== 'DONE' &&
        task.status !== 'CANCELLED'
      ).sort((a, b) => (a.dueDate ?? '').localeCompare(b.dueDate ?? '')).slice(0, 5);
  }

  get overdueTasks(): Task[] {
    const today = this.getTodayDate();

    return this.tasks
      .filter(task =>
        task.dueDate !== null &&
        task.dueDate < today &&
        task.status !== 'DONE' &&
        task.status !== 'CANCELLED'
      ).sort((a, b) => (b.dueDate ?? '').localeCompare(a.dueDate ?? ''));
  }

}
