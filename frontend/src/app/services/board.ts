import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class Board {

  private readonly apiUrl = `${environment.apiUrl}/boards`;

  constructor(private http: HttpClient) {
  }

}
