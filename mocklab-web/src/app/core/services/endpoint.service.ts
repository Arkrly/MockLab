import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Endpoint, CreateEndpointRequest } from '../models/endpoint.model';

@Injectable({ providedIn: 'root' })
export class EndpointService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private apiUrl(workspaceId: number): string {
    return `${this.baseUrl}/api/workspaces/${workspaceId}/endpoints`;
  }

  getByWorkspace(workspaceId: number): Observable<Endpoint[]> {
    return this.http.get<Endpoint[]>(this.apiUrl(workspaceId));
  }

  getById(workspaceId: number, endpointId: number): Observable<Endpoint> {
    return this.http.get<Endpoint>(`${this.apiUrl(workspaceId)}/${endpointId}`);
  }

  create(workspaceId: number, request: CreateEndpointRequest): Observable<Endpoint> {
    return this.http.post<Endpoint>(this.apiUrl(workspaceId), request);
  }

  update(workspaceId: number, endpointId: number, request: CreateEndpointRequest): Observable<Endpoint> {
    return this.http.put<Endpoint>(`${this.apiUrl(workspaceId)}/${endpointId}`, request);
  }

  delete(workspaceId: number, endpointId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl(workspaceId)}/${endpointId}`);
  }

  toggleStateful(workspaceId: number, endpoint: Endpoint): Observable<Endpoint> {
    return this.update(workspaceId, endpoint.id, {
      method: endpoint.method,
      path: endpoint.path,
      responseBody: endpoint.responseBody,
      statusCode: endpoint.statusCode,
      latencyMs: endpoint.latencyMs,
      statefulEnabled: !endpoint.statefulEnabled
    });
  }
}
