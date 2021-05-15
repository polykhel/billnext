import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { accountState } from 'app/account/account.route';
import { PasswordResetFinishComponent } from 'app/account/password-reset/finish/password-reset-finish.component';
import { PasswordResetInitComponent } from 'app/account/password-reset/init/password-reset-init.component';
import { PasswordStrengthBarComponent } from 'app/account/password/password-strength-bar/password-strength-bar.component';
import { PasswordComponent } from 'app/account/password/password.component';
import { RegisterComponent } from 'app/account/register/register.component';
import { SharedModule } from 'app/shared/shared.module';

import { ActivateComponent } from './activate/activate.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild(accountState)],
  exports: [],
  declarations: [
    ActivateComponent,
    PasswordComponent,
    PasswordStrengthBarComponent,
    PasswordResetInitComponent,
    PasswordResetFinishComponent,
    RegisterComponent,
  ],
  providers: [],
})
export class AccountModule {}
