import { Component, Output, EventEmitter } from '@angular/core';
import { LoginService } from 'app/shared/login/login.service';
import { Router } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { ThemeService } from 'app/shared/theme/theme.service';
import { LANGUAGES } from 'app/config/language.constants';
import { SessionStorageService } from 'ngx-webstorage';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.less'],
})
export class HeaderComponent {
  isCollapsed = false;
  languages = LANGUAGES;
  currentLanguage = 'en';

  @Output() toggleNavbarEvent = new EventEmitter<boolean>();

  constructor(
    private loginService: LoginService,
    private accountService: AccountService,
    private router: Router,
    private themeService: ThemeService,
    private sessionStorage: SessionStorageService,
    private translateService: TranslateService
  ) {}

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
    this.router.navigate(['']);
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  changeLanguage(languageKey: string): void {
    this.sessionStorage.store('locale', languageKey);
    this.translateService.use(languageKey);
    this.currentLanguage = languageKey;
  }
}
