import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateTask } from '../models/create-task';
import { UpdateTask } from '../models/update-task';
import { TaskPage } from '../models/task-page';
import { Task } from '../models/task';
import { TaskFilters } from '../models/task-filters';
import { TaskStatus } from '../models/task-status';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private readonly apiUrl = `${environment.apiUrl}/api/tasks`;

  constructor(private http: HttpClient) {}

  getById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  getTasks(filters: TaskFilters): Observable<TaskPage> {

    let params = new HttpParams()
      .set('boardId', filters.boardId)
      .set('page', filters.page)
      .set('size', filters.size)
      .set('sortBy', filters.sortBy)
      .set('direction', filters.direction);

    if (filters.search) {
      params = params.set('search', filters.search);
    }

    if (filters.status) {
      params = params.set('status', filters.status);
    }

    if (filters.priority) {
      params = params.set('priority', filters.priority);
    }

    if (filters.assignedUserId !== undefined) {
      params = params.set('assignedUserId', filters.assignedUserId);
    }

    if (filters.labelId !== undefined) {
      params = params.set('labelId', filters.labelId);
    }

    if (filters.dueDateFrom) {
      params = params.set('dueDateFrom', filters.dueDateFrom);
    }

    if (filters.dueDateTo) {
      params = params.set('dueDateTo', filters.dueDateTo);
    }

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

  unassignTask(taskId: number): Observable<Task> {
    return this.http.delete<Task>(
      `${this.apiUrl}/${taskId}/assign`
    );
  }

  addLabel(taskId: number, labelId: number): Observable<Task> {
    return this.http.post<Task>(
      `${this.apiUrl}/${taskId}/labels/${labelId}`,
      {}
    );
  }

  removeLabel(taskId: number, labelId: number): Observable<Task> {
    return this.http.delete<Task>(
      `${this.apiUrl}/${taskId}/labels/${labelId}`
    );
  }

  updateStatus(
    taskId: number,
    status: TaskStatus
  ): Observable<Task> {
    return this.http.put<Task>(
      `${this.apiUrl}/${taskId}/status`,
      {},
      { params: { status: status } }
    );
  }
}
