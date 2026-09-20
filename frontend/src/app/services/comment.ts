import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateComment } from '../models/create-comment';
import { Comment } from '../models/comment';
import { UpdateComment } from '../models/update-comment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private readonly apiUrl = `${environment.apiUrl}/api/comments`;

  constructor(private http: HttpClient) {}

  getByTask(taskId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(
      `${this.apiUrl}/task/${taskId}`
    );
  }

  create(comment: CreateComment): Observable<Comment> {
    return this.http.post<Comment>(this.apiUrl, comment);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  update(id: number, comment: UpdateComment): Observable<Comment> {
    return this.http.put<Comment>(`${this.apiUrl}/${id}`, comment);
  }
}
