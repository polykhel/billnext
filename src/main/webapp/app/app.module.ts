import { NgModule, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import locale from '@angular/common/locales/en';
import { BrowserModule, Title } from '@angular/platform-browser';
import { ServiceWorkerModule } from '@angular/service-worker';
import { NgxWebstorageModule } from 'ngx-webstorage';
import { TranslateModule, TranslateService, TranslateLoader, MissingTranslationHandler } from '@ngx-translate/core';

import { SharedModule } from 'app/shared/shared.module';
import { AppRoutingModule } from './app-routing.module';
import { HomeModule } from './modules/home/home.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { httpInterceptorProviders } from './core/interceptor';
import { translatePartialLoader, missingTranslationHandler } from './config/translation.config';
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { DateFnsModule } from 'ngx-date-fns';
import { HeaderComponent } from 'app/layouts/header/header.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppInitializerProvider } from 'app/app-initializer.service';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    SharedModule,
    HomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    AppRoutingModule,
    // Set this to true to enable service worker (PWA)
    ServiceWorkerModule.register('ngsw-worker.js', { enabled: false }),
    HttpClientModule,
    NgxWebstorageModule.forRoot({ prefix: 'app', separator: '-' }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: translatePartialLoader,
        deps: [HttpClient],
      },
      missingTranslationHandler: {
        provide: MissingTranslationHandler,
        useFactory: missingTranslationHandler,
      },
    }),
    DateFnsModule.forRoot(),
  ],
  providers: [AppInitializerProvider, Title, { provide: LOCALE_ID, useValue: 'en' }, httpInterceptorProviders],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent, HeaderComponent, DashboardComponent],
  bootstrap: [MainComponent],
})
export class AppModule {
  constructor(translateService: TranslateService) {
    registerLocaleData(locale);
    translateService.setDefaultLang('en');
    translateService.use('en');
  }
}
