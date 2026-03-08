import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html'
})
export class SidebarComponent {
  userEmail: string = '';
  userPlan: string = 'FREE';

  constructor(private authService: AuthService) {
    const user = this.authService.getCurrentUser();
    this.userEmail = user?.email || '';
    this.userPlan = user?.plan || 'FREE';
  }
}
