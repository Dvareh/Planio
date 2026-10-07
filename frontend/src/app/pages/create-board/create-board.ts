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
  boardDescription = '';
  boardKey = '';

  constructor(private boardService: BoardService,
              private router: Router,) {}

  createBoard(): void {
    const board: CreateBoard = {
      name: this.boardName,
      description: this.boardDescription
    };

    if (this.boardKey.trim()) {
      board.key = this.boardKey.trim();
    }

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

  get suggestedKey(): string {

    const name = this.boardName.trim().toUpperCase().replace(/[^A-Z0-9 ]/g, ' ').replace(/\s+/g, ' ');

    if (!name) {
      return 'DTK';
    }

    const words = name.split(' ');

    if (words.length > 1) {
      let key = '';
      for (const word of words) {
        if (word) {
          key += word.charAt(0);
        }
        if (key.length === 5) {
          break;
        }
      }

      return key.length >= 2 ? key : 'DTK';
    }

    const key = words[0].substring(0, Math.min(4, words[0].length));

    return key.length >= 2 ? key : 'DTK';
  }

}
