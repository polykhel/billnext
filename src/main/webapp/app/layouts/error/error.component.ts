import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
})
export class ErrorComponent implements OnInit, OnDestroy {
  status?: 'success' | 'error' | 'info' | 'warning' | '404' | '403' | '500';
  title?: string;
  titleKey?: string;
  errorMessage?: string;
  errorKey?: string;
  langChangeSubscription?: Subscription;

  constructor(private translateService: TranslateService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.data.subscribe(routeData => {
      if (routeData.status) {
        this.status = routeData.status;
      } else {
        this.status = 'error';
      }

      this.titleKey = `error.title.${this.status}`;
      this.errorKey = `error.http.${this.status}`;

      this.getErrorMessageTranslation();
      this.langChangeSubscription = this.translateService.onLangChange.subscribe(() => this.getErrorMessageTranslation());
    });
  }

  ngOnDestroy(): void {
    if (this.langChangeSubscription) {
      this.langChangeSubscription.unsubscribe();
    }
  }

  private getErrorMessageTranslation(): void {
    this.title = '';
    this.errorMessage = '';

    if (this.titleKey) {
      this.translateService.get(this.titleKey).subscribe(translatedTitle => {
        this.title = translatedTitle;
      });
    }
    if (this.errorKey) {
      this.translateService.get(this.errorKey).subscribe(translatedErrorMessage => {
        this.errorMessage = translatedErrorMessage;
      });
    }
  }
}
