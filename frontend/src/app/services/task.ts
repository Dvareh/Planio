import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateTask } from '../models/create-task';
import { UpdateTask } from '../models/update-task';
import { TaskPage } from '../models/task-page';
import { Task } from '../models/task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private readonly apiUrl = `${environment.apiUrl}/api/tasks`;

  constructor(private http: HttpClient) {}

  getById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  getTasks(
    boardId: number,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'dueDate',
    direction: string = 'asc'
  ): Observable<TaskPage> {
    const params = new HttpParams()
      .set('boardId', boardId)
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<TaskPage>(this.apiUrl, { params });
  }

  getMyTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/my`);
  }

  create(task: CreateTask): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  update(id: number, task: UpdateTask): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  assignTask(taskId: number, userId: number): Observable<Task> {
    return this.http.put<Task>(
      `${this.apiUrl}/${taskId}/assign/${userId}`,
      {}
    );
  }
}
