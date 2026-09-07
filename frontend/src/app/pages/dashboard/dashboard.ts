import { Component, OnInit } from '@angular/core';
import { BoardService } from '../../services/board';
import { Board as BoardModel } from '../../models/board';
import { ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule} from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {

  boards: BoardModel[] = [];

  constructor(private boardService: BoardService,
              private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    console.log('Dashboard initialized');
    this.loadBoards();
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
      }
    });
  }

  deleteBoardId: number | null = null;
  deleteBoardName = '';
  deleteConfirmation = '';

  confirmDeleteBoard(board: BoardModel): void {
    this.deleteBoardId = board.id;
    this.deleteBoardName = board.name;
    this.deleteConfirmation = '';
  }

  deleteBoard(): void {
    if (
      this.deleteBoardId === null ||
      this.deleteConfirmation !== this.deleteBoardName
    ) {
      return;
    }

    this.boardService.delete(this.deleteBoardId).subscribe({
      next: () => {
        this.boards = this.boards.filter(
          board => board.id !== this.deleteBoardId
        );

        this.deleteBoardId = null;
        this.deleteBoardName = '';
        this.deleteConfirmation = '';

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Failed to delete board:', error);
      }
    });
  }

}
