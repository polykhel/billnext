import { Routes } from '@angular/router';
import { passwordResetFinishRoute } from 'app/account/password-reset/finish/password-reset-finish.route';
import { passwordResetInitRoute } from 'app/account/password-reset/init/password-reset-init.route';
import { passwordRoute } from 'app/account/password/password.route';

import { activateRoute } from './activate/activate.route';

const ACCOUNT_ROUTES = [activateRoute, passwordRoute, passwordResetInitRoute, passwordResetFinishRoute];

export const accountState: Routes = [
  {
    path: '',
    children: ACCOUNT_ROUTES,
  },
];
