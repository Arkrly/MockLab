export interface Endpoint {
  id: number;
  workspaceId: number;
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
  path: string;
  responseBody: string;
  statusCode: number;
  latencyMs: number;
  statefulEnabled: boolean;
  createdAt: string;
}

export interface CreateEndpointRequest {
  method: string;
  path: string;
  responseBody: string;
  statusCode: number;
  latencyMs: number;
  statefulEnabled: boolean;
}
