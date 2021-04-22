import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'dashboard',
        data: {
          pathTitle: 'dashboard.title',
        },
        loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule),
      },
      {
        path: 'wallet-group',
        data: {
          pageTitle: 'walletGroup.home.title',
        },
        loadChildren: () => import('./wallet-group/wallet-group.module'),
      },
    ]),
  ],
})
export class ModulesRoutingModule {}
