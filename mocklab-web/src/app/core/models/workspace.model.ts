export interface Workspace {
  id: number;
  name: string;
  apiKey: string;
  ownerId: number;
  ownerName: string;
  createdAt: string;
}

export interface CreateWorkspaceRequest {
  name: string;
}
