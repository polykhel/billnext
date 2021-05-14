import { Routes } from '@angular/router';
import { passwordResetInitRoute } from 'app/account/password-reset/init/password-reset-init.route';
import { passwordRoute } from 'app/account/password/password.route';

import { activateRoute } from './activate/activate.route';

const ACCOUNT_ROUTES = [activateRoute, passwordRoute, passwordResetInitRoute];

export const accountState: Routes = [
  {
    path: '',
    children: ACCOUNT_ROUTES,
  },
];
