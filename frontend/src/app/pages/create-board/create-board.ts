import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BoardService } from '../../services/board';
import { CreateBoard } from '../../models/create-board';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-board',
  imports: [FormsModule,],
  templateUrl: './create-board.html',
  styleUrl: './create-board.css',
})
export class CreateBoardPage {

  boardName = '';

  constructor(private boardService: BoardService,
              private router: Router,) {}

  createBoard(): void {
    const board: CreateBoard = {
      name: this.boardName
    };

    this.boardService.create(board).subscribe({
      next: (board) => {
        console.log('Board created:', board);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Failed to create board:', error);
      }
    });
  }

}
