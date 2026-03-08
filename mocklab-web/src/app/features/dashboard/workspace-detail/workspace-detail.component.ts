import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { WorkspaceService } from '../../../core/services/workspace.service';
import { EndpointService } from '../../../core/services/endpoint.service';
import { Workspace } from '../../../core/models/workspace.model';
import { Endpoint } from '../../../core/models/endpoint.model';
import { RequestLogsComponent } from '../request-logs/request-logs.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { MethodColorPipe } from '../../../shared/pipes/method-color.pipe';

@Component({
  selector: 'app-workspace-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, RequestLogsComponent, EmptyStateComponent, LoadingSpinnerComponent, MethodColorPipe],
  templateUrl: './workspace-detail.component.html'
})
export class WorkspaceDetailComponent implements OnInit {
  workspace: Workspace | null = null;
  endpoints: Endpoint[] = [];
  isLoading: boolean = true;
  activeTab: 'endpoints' | 'logs' = 'endpoints';
  copiedKey: boolean = false;
  workspaceId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private workspaceService: WorkspaceService,
    private endpointService: EndpointService
  ) {}

  ngOnInit(): void {
    this.workspaceId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadWorkspace();
    this.loadEndpoints();
  }

  loadWorkspace(): void {
    this.workspaceService.getById(this.workspaceId).subscribe({
      next: (ws) => this.workspace = ws,
      error: () => this.router.navigate(['/dashboard'])
    });
  }

  loadEndpoints(): void {
    this.isLoading = true;
    this.endpointService.getByWorkspace(this.workspaceId).subscribe({
      next: (endpoints) => {
        this.endpoints = endpoints;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  copyApiKey(): void {
    if (this.workspace) {
      navigator.clipboard.writeText(this.workspace.apiKey);
      this.copiedKey = true;
      setTimeout(() => this.copiedKey = false, 2000);
    }
  }

  toggleStateful(endpoint: Endpoint): void {
    this.endpointService.toggleStateful(this.workspaceId, endpoint).subscribe({
      next: (updated) => {
        const idx = this.endpoints.findIndex(e => e.id === updated.id);
        if (idx !== -1) this.endpoints[idx] = updated;
      }
    });
  }

  deleteEndpoint(endpoint: Endpoint): void {
    if (confirm(`Delete endpoint ${endpoint.method} ${endpoint.path}?`)) {
      this.endpointService.delete(this.workspaceId, endpoint.id).subscribe({
        next: () => this.endpoints = this.endpoints.filter(e => e.id !== endpoint.id)
      });
    }
  }

  regenerateApiKey(): void {
    if (confirm('Regenerate API key? The old key will stop working immediately.')) {
      this.workspaceService.generateNewApiKey(this.workspaceId).subscribe({
        next: (ws) => this.workspace = ws
      });
    }
  }
}
