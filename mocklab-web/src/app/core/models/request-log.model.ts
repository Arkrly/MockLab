export interface RequestLog {
  id: number;
  workspaceId: number;
  endpointId: number | null;
  method: string;
  path: string;
  requestHeaders: string;
  requestBody: string;
  responseStatus: number;
  matched: boolean;
  loggedAt: string;
}
