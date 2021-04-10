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
    ]),
  ],
})
export class ModulesRoutingModule {}
