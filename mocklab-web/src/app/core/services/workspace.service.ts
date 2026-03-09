import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Workspace, CreateWorkspaceRequest } from '../models/workspace.model';

@Injectable({ providedIn: 'root' })
export class WorkspaceService {
  private readonly apiUrl = `${environment.apiUrl}/api/workspaces`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Workspace[]> {
    return this.http.get<Workspace[]>(this.apiUrl);
  }

  getById(id: number): Observable<Workspace> {
    return this.http.get<Workspace>(`${this.apiUrl}/${id}`);
  }

  create(request: CreateWorkspaceRequest): Observable<Workspace> {
    return this.http.post<Workspace>(this.apiUrl, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  generateNewApiKey(workspaceId: number): Observable<Workspace> {
    return this.http.post<Workspace>(`${this.apiUrl}/${workspaceId}/regenerate-key`, {});
  }

  addMember(workspaceId: number, userId: number, role: string = 'CONSUMER'): Observable<Workspace> {
    return this.http.post<Workspace>(
      `${this.apiUrl}/${workspaceId}/members?userId=${userId}&role=${role}`, {}
    );
  }
}
