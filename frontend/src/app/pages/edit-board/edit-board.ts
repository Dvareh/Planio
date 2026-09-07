import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BoardService } from '../../services/board';
import { UpdateBoard } from '../../models/update-board';

@Component({
  selector: 'app-edit-board',
  imports: [FormsModule],
  templateUrl: './edit-board.html',
  styleUrl: './edit-board.css',
})
export class EditBoard implements OnInit {

  boardId!: number;
  boardName = '';

  constructor(private route: ActivatedRoute,
              private boardService: BoardService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.boardId = Number(this.route.snapshot.paramMap.get('id'));

    this.boardService.getById(this.boardId).subscribe({
      next: (board) => {
        this.boardName = board.name;
      },
      error: (error) => {
        console.error('Failed to load board', error);
      }
    });
  }

  updateBoard(): void {
    const board : UpdateBoard = {
      name: this.boardName,
    };

    this.boardService.update(this.boardId, board).subscribe({
      next: (board) => {
        console.log('Board updated', board);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Failed to update board', error);
      }
    });
  }
}
