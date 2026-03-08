import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  templateUrl: './status-badge.component.html'
})
export class StatusBadgeComponent implements OnInit {
  @Input() statusCode: number = 200;
  colorClass: string = '';

  ngOnInit(): void {
    if (this.statusCode >= 200 && this.statusCode < 300) {
      this.colorClass = 'bg-green-100 text-green-700';
    } else if (this.statusCode >= 400 && this.statusCode < 500) {
      this.colorClass = 'bg-yellow-100 text-yellow-700';
    } else if (this.statusCode >= 500) {
      this.colorClass = 'bg-red-100 text-red-700';
    } else {
      this.colorClass = 'bg-gray-100 text-gray-700';
    }
  }
}
