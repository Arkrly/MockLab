import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './settings.component.html'
})
export class SettingsComponent {
  userEmail: string = '';
  userName: string = '';
  userPlan: string = 'FREE';

  constructor(private authService: AuthService) {
    const user = this.authService.getCurrentUser();
    this.userEmail = user?.email || '';
    this.userName = user?.name || '';
    this.userPlan = user?.plan || 'FREE';
  }
}
