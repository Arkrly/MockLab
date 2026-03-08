import { Component, Input, OnInit, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription, interval, switchMap } from 'rxjs';
import { RequestLogService } from '../../../core/services/request-log.service';
import { RequestLog } from '../../../core/models/request-log.model';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { MethodColorPipe } from '../../../shared/pipes/method-color.pipe';
import { TimeAgoPipe } from '../../../shared/pipes/time-ago.pipe';

@Component({
  selector: 'app-request-logs',
  standalone: true,
  imports: [CommonModule, EmptyStateComponent, StatusBadgeComponent, MethodColorPipe, TimeAgoPipe],
  templateUrl: './request-logs.component.html'
})
export class RequestLogsComponent implements OnInit, OnDestroy {
  @Input() workspaceId: number = 0;
  @ViewChild('logContainer') logContainer!: ElementRef;

  logs: RequestLog[] = [];
  isLoading: boolean = true;
  private pollSubscription?: Subscription;

  constructor(private requestLogService: RequestLogService) {}

  ngOnInit(): void {
    this.loadLogs();

    // Poll every 5 seconds
    this.pollSubscription = interval(5000).pipe(
      switchMap(() => this.requestLogService.getByWorkspace(this.workspaceId))
    ).subscribe({
      next: (logs) => {
        const hadNewLogs = logs.length > this.logs.length;
        this.logs = logs;
        if (hadNewLogs) {
          setTimeout(() => this.scrollToBottom(), 100);
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.pollSubscription?.unsubscribe();
  }

  private loadLogs(): void {
    this.isLoading = true;
    this.requestLogService.getByWorkspace(this.workspaceId).subscribe({
      next: (logs) => {
        this.logs = logs;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  private scrollToBottom(): void {
    if (this.logContainer?.nativeElement) {
      const el = this.logContainer.nativeElement;
      el.scrollTop = el.scrollHeight;
    }
  }
}
