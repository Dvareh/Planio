import { Component, OnInit } from '@angular/core';
import { BoardService } from '../../services/board';
import { Board as BoardModel } from '../../models/board';
import { ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule} from '@angular/forms';
import {Task} from '../../models/task';
import {TaskService} from '../../services/task';
import {DatePipe} from '@angular/common';
import { TaskStatusLabelPipe } from '../../pipes/task-status-label-pipe';
import { TaskDeadlinePipe } from '../../pipes/task-deadline-pipe';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, FormsModule, DatePipe, TaskStatusLabelPipe, TaskDeadlinePipe],
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

  deleteBoardId: number | null = null;
  deleteBoardName = '';
  deleteConfirmation = '';

  constructor(
    private boardService: BoardService,
    private cdr: ChangeDetectorRef,
    private taskService: TaskService,
  ) {}

  ngOnInit(): void {
    this.loadBoards();
    this.loadTasks();
  }

  loadBoards(): void {
    console.log('Loading boards...');

    this.boardService.getMyBoards().subscribe({
      next: (boards) => {
        this.boards = boards;
        this.cdr.detectChanges();
        console.log('Boards received:', boards);
      },
      error: (error) => {
        console.error('Failed to load boards:', error);
      },
    });
  }

  loadTasks(): void {
    this.taskService.getMyTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.calculateStatistics();
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load tasks:', error);
      },
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

  get upcomingTasks(): Task[] {
    const currentTime = new Date().getTime();

    return this.tasks
      .filter(task =>
        task.status !== 'DONE' &&
        task.status !== 'CANCELLED' &&
        new Date(task.dueDate).getTime() >= currentTime
      )
      .sort((a, b) => {
        return new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime();
      })
      .slice(0, 5);
  }

  get overdueTasks(): Task[] {
    const currentTime = new Date().getTime();

    return this.tasks
      .filter(task =>
        task.status !== 'DONE' &&
        task.status !== 'CANCELLED' &&
        new Date(task.dueDate).getTime() < currentTime
      )
      .sort((a, b) => {
        return new Date(b.dueDate).getTime() - new Date(a.dueDate).getTime();
      });
  }

  confirmDeleteBoard(board: BoardModel): void {
    this.deleteBoardId = board.id;
    this.deleteBoardName = board.name;
    this.deleteConfirmation = '';
  }

  deleteBoard(): void {
    if (this.deleteBoardId === null || this.deleteConfirmation !== this.deleteBoardName) {
      return;
    }

    this.boardService.delete(this.deleteBoardId).subscribe({
      next: () => {
        this.boards = this.boards.filter((board) => board.id !== this.deleteBoardId);

        this.deleteBoardId = null;
        this.deleteBoardName = '';
        this.deleteConfirmation = '';

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Failed to delete board:', error);
      },
    });
  }
}
