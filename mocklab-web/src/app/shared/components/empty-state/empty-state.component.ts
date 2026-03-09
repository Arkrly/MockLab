import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  templateUrl: './empty-state.component.html'
})
export class EmptyStateComponent {
  @Input() title: string = 'Nothing here yet';
  @Input() message: string = 'Get started by creating your first item.';
  @Input() icon: string = 'inbox';
}
