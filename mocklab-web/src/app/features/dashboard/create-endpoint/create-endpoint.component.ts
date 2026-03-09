import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EndpointService } from '../../../core/services/endpoint.service';

@Component({
  selector: 'app-create-endpoint',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './create-endpoint.component.html'
})
export class CreateEndpointComponent {
  endpointForm: FormGroup;
  workspaceId: number;
  isLoading: boolean = false;
  errorMessage: string = '';

  httpMethods = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private endpointService: EndpointService
  ) {
    this.workspaceId = Number(this.route.snapshot.paramMap.get('id'));

    this.endpointForm = this.fb.group({
      method: ['GET', Validators.required],
      path: ['', [Validators.required]],
      responseBody: ['{\n  "message": "Hello from MockLab"\n}'],
      statusCode: [200, [Validators.required, Validators.min(100), Validators.max(599)]],
      latencyMs: [0, [Validators.required, Validators.min(0), Validators.max(3000)]],
      statefulEnabled: [false]
    });
  }

  get latencyValue(): number {
    return this.endpointForm.get('latencyMs')?.value || 0;
  }

  onSubmit(): void {
    if (this.endpointForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    this.endpointService.create(this.workspaceId, this.endpointForm.value).subscribe({
      next: () => {
        this.router.navigate(['/dashboard/workspace', this.workspaceId]);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Failed to create endpoint.';
      }
    });
  }
}
