import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WorkspaceService } from '../../../core/services/workspace.service';
import { EndpointService } from '../../../core/services/endpoint.service';
import { Workspace } from '../../../core/models/workspace.model';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, EmptyStateComponent, LoadingSpinnerComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  workspaces: Workspace[] = [];
  endpointCounts: Record<number, number> = {};
  isLoading: boolean = true;
  showCreateModal: boolean = false;
  createForm: FormGroup;
  copiedId: number | null = null;

  constructor(
    private workspaceService: WorkspaceService,
    private endpointService: EndpointService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.createForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]]
    });
  }

  ngOnInit(): void {
    this.loadWorkspaces();
  }

  loadWorkspaces(): void {
    this.isLoading = true;
    this.workspaceService.getAll().subscribe({
      next: (workspaces) => {
        this.workspaces = workspaces;
        this.isLoading = false;
        workspaces.forEach(ws => {
          this.endpointService.getByWorkspace(ws.id).subscribe({
            next: (endpoints) => this.endpointCounts[ws.id] = endpoints.length,
            error: () => this.endpointCounts[ws.id] = 0
          });
        });
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  createWorkspace(): void {
    if (this.createForm.invalid) return;
    this.workspaceService.create(this.createForm.value).subscribe({
      next: () => {
        this.showCreateModal = false;
        this.createForm.reset();
        this.loadWorkspaces();
      }
    });
  }

  maskApiKey(key: string): string {
    if (!key) return '';
    return key.substring(0, 8) + '••••••••';
  }

  copyApiKey(workspace: Workspace): void {
    navigator.clipboard.writeText(workspace.apiKey);
    this.copiedId = workspace.id;
    setTimeout(() => this.copiedId = null, 2000);
  }

  openWorkspace(id: number): void {
    this.router.navigate(['/dashboard/workspace', id]);
  }
}
