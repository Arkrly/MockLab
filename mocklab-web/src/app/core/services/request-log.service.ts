import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RequestLog } from '../models/request-log.model';

@Injectable({ providedIn: 'root' })
export class RequestLogService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getByWorkspace(workspaceId: number): Observable<RequestLog[]> {
    return this.http.get<RequestLog[]>(
      `${this.baseUrl}/api/workspaces/${workspaceId}/endpoints/0/logs`
    );
  }

  getByEndpoint(workspaceId: number, endpointId: number): Observable<RequestLog[]> {
    return this.http.get<RequestLog[]>(
      `${this.baseUrl}/api/workspaces/${workspaceId}/endpoints/${endpointId}/logs`
    );
  }
}
