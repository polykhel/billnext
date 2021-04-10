import { Routes } from '@angular/router';

import { ErrorComponent } from './error.component';

export const errorRoute: Routes = [
  {
    path: 'error',
    component: ErrorComponent,
    data: {
      pageTitle: 'error.title',
    },
  },
  {
    path: 'bad-request',
    component: ErrorComponent,
    data: {
      status: '400',
      pageTitle: 'error.title.400',
    },
  },
  {
    path: 'access-denied',
    component: ErrorComponent,
    data: {
      status: '403',
      pageTitle: 'error.title.403',
    },
  },
  {
    path: '404',
    component: ErrorComponent,
    data: {
      status: '404',
      pageTitle: 'error.title.404',
    },
  },
  {
    path: '500',
    component: ErrorComponent,
    data: {
      status: '500',
      pageTitle: 'error.title.500',
    },
  },
  {
    path: '**',
    redirectTo: '/404',
  },
];
