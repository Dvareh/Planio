import { Component, OnInit } from '@angular/core';

import { Board as BoardModel} from '../../models/board'
import { BoardService } from '../../services/board';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-board',
  imports: [RouterLink],
  templateUrl: './board.html',
  styleUrl: './board.css',
})
export class Board implements OnInit {
  boardId!: number;
  board: BoardModel | null = null;

  constructor(
    private route: ActivatedRoute,
    private boardService: BoardService,
  ) {}

  ngOnInit(): void {
    this.boardId = Number(this.route.snapshot.paramMap.get('id'));

    this.loadBoard();
  }

  loadBoard(): void {
    this.boardService.getById(this.boardId).subscribe({
      next: (board) => {
        console.log('Board received:', board);
        this.board = board;
      },
      error: (error) => {
        console.error('Failed to load board', error);
      },
    });
  }
}
