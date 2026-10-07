import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { BoardService } from '../../services/board';
import { Auth } from '../../services/auth';
import { Board } from '../../models/board';
import { User } from '../../models/user';
import { Label } from '../../models/label';
import { LabelService } from '../../services/label';

@Component({
  selector: 'app-board-settings',
  imports: [FormsModule, RouterLink],
  templateUrl: './board-settings.html',
  styleUrl: './board-settings.css',
})
export class BoardSettings implements OnInit {

  boardId!: number;

  board: Board | null = null;
  participants: User[] = [];

  name = '';
  description = '';

  isLoading = true;
  isSaving = false;
  isDeleting = false;

  successMessage = '';
  errorMessage = '';

  participantEmail = '';
  participantMessage = '';
  participantError = '';

  labels: Label[] = [];

  newLabelName = '';

  labelMessage = '';
  labelError = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private boardService: BoardService,
    private auth: Auth,
    private changeDetectorRef: ChangeDetectorRef,
    private labelService: LabelService,
  ) {}

  ngOnInit(): void {
    this.boardId = Number(this.route.snapshot.paramMap.get('id'));

    this.auth.loadCurrentUser().subscribe({
      next: () => {
        this.loadBoard();
      }
    });
  }

  loadBoard(): void {
    this.boardService.getById(this.boardId).subscribe({
      next: (board) => {
        this.board = board;
        this.name = board.name;
        this.description = board.description || '';

        this.loadParticipants();
        this.loadLabels();

        this.isLoading = false;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load board', error);

        this.errorMessage = 'Failed to load board.';
        this.isLoading = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  loadParticipants(): void {
    this.boardService.getParticipants(this.boardId).subscribe({
      next: (participants) => {
        this.participants = participants;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load participants', error);
      }
    });
  }

  loadLabels(): void {

    this.labelService.getBoardLabels(this.boardId).subscribe({
      next: (labels) => {
        this.labels = labels;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load labels', error);

        this.labelError = 'Failed to load labels.';

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  isOwner(): boolean {
    return this.board?.ownerId === this.auth.currentUser?.id;
  }

  saveChanges(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (!this.name.trim()) {
      this.errorMessage = 'Board name cannot be empty.';
      return;
    }

    this.isSaving = true;

    this.boardService.update(this.boardId, {
      name: this.name.trim(),
      description: this.description.trim()
    }).subscribe({
      next: (board) => {
        this.board = board;
        this.name = board.name;
        this.description = board.description || '';

        this.successMessage = 'Board updated successfully.';
        this.isSaving = false;

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to update board', error);

        this.errorMessage = 'Failed to update board.';
        this.isSaving = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  addParticipant(): void {
    this.participantMessage = '';
    this.participantError = '';

    if (!this.participantEmail.trim()) {
      this.participantError = 'Enter an email address.';
      return;
    }

    this.boardService.addParticipant(
      this.boardId,
      this.participantEmail.trim()
    ).subscribe({
      next: () => {
        this.participantMessage = 'Participant added successfully.';
        this.participantEmail = '';

        this.loadParticipants();

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to add participant', error);

        this.participantError = 'Failed to add participant.';

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  removeParticipant(userId: number): void {
    this.participantMessage = '';
    this.participantError = '';

    this.boardService.removeParticipant(
      this.boardId,
      userId
    ).subscribe({
      next: () => {
        this.participantMessage = 'Participant removed successfully.';

        this.loadParticipants();

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to remove participant', error);

        this.participantError = 'Failed to remove participant.';

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  deleteBoard(): void {
    const confirmed = confirm(
      'Are you sure you want to delete this board? This action cannot be undone.'
    );

    if (!confirmed) {
      return;
    }

    this.isDeleting = true;
    this.errorMessage = '';

    this.boardService.delete(this.boardId).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Failed to delete board', error);

        this.errorMessage = 'Failed to delete board.';
        this.isDeleting = false;

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  createLabel(): void {

    this.labelMessage = '';
    this.labelError = '';

    const name = this.newLabelName.trim();

    if (!name) {
      this.labelError = 'Label name is required.';
      return;
    }

    this.labelService.createLabel(this.boardId, name).subscribe({
      next: () => {
        this.newLabelName = '';

        this.labelMessage = 'Label created successfully.';

        this.loadLabels();

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to create label', error);

        if (error.status === 409) {
          this.labelError = 'A label with this name already exists.';
        } else {
          this.labelError = 'Failed to create label.';
        }

        this.changeDetectorRef.detectChanges();
      }
    });
  }

  deleteLabel(labelId: number): void {

    const confirmed = confirm(
      'Are you sure you want to delete this label?'
    );

    if (!confirmed) {
      return;
    }

    this.labelMessage = '';
    this.labelError = '';

    this.labelService.deleteLabel(this.boardId, labelId).subscribe({
      next: () => {

        this.labelMessage = 'Label deleted successfully.';

        this.loadLabels();

        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Failed to delete label', error);

        this.labelError =
          'Failed to delete label.';

        this.changeDetectorRef.detectChanges();
      }
    });
  }
}
