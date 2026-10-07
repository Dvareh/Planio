import { Service } from '@angular/core';

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../environments/environment';
import { Label } from '../models/label';

@Injectable({
  providedIn: 'root'
})
export class LabelService {

  private readonly apiUrl = `${environment.apiUrl}/api/boards`;

  constructor(private http: HttpClient) {
  }

  getBoardLabels(boardId: number): Observable<Label[]> {
    return this.http.get<Label[]>(
      `${this.apiUrl}/${boardId}/labels`
    );
  }

  createLabel(boardId: number, name: string): Observable<Label> {

    return this.http.post<Label>(
      `${this.apiUrl}/${boardId}/labels`,
      {
        name: name
      }
    );
  }

  deleteLabel(boardId: number, labelId: number): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${boardId}/labels/${labelId}`
    );
  }
}
