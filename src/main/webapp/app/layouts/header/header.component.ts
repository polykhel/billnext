import { Component, Output, EventEmitter } from '@angular/core';
import { LoginService } from 'app/shared/login/login.service';
import { Router } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  isCollapsed = false;

  @Output() toggleNavbarEvent = new EventEmitter<boolean>();

  constructor(private loginService: LoginService, private accountService: AccountService, private router: Router) {}

  toggleNavbar(): void {
    this.isCollapsed = !this.isCollapsed;
    this.toggleNavbarEvent.emit(this.isCollapsed);
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginService.login();
  }

  logout(): void {
    this.loginService.logout();
    this.router.navigate(['']).then();
  }
}
