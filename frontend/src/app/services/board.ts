import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { Board } from '../models/board';
import { Observable } from 'rxjs';
import { CreateBoard } from '../models/create-board';
import { UpdateBoard } from '../models/update-board';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class BoardService {

  private readonly apiUrl = `${environment.apiUrl}/api/boards`;

  constructor(private http: HttpClient) {
  }

  getMyBoards(): Observable<Board[]> {
    return this.http.get<Board[]>(`${this.apiUrl}/my`);
  }

  getById(id: number): Observable<Board> {
    return this.http.get<Board>(`${this.apiUrl}/${id}`);
  }

  create(board: CreateBoard): Observable<Board> {
    return this.http.post<Board>(this.apiUrl, board);
  }

  update(id: number, board: UpdateBoard): Observable<Board> {
    return this.http.put<Board>(`${this.apiUrl}/${id}`, board);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getParticipants(boardId: number): Observable<User[]> {
    return this.http.get<User[]>(
      `${this.apiUrl}/${boardId}/participants`
    );
  }

  addParticipant(boardId: number, email: string): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/${boardId}/participants`,
      null,
      {
        params: {
          email
        }
      }
    );
  }

  removeParticipant(boardId: number, userId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${boardId}/participants/${userId}`
    );
  }
}
