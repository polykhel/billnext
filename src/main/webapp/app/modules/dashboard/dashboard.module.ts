import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DashboardComponent } from 'app/modules/dashboard/dashboard.component';
import { RouterModule } from '@angular/router';
import { dashboardRoute } from 'app/modules/dashboard/dashboard.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([dashboardRoute])],
  declarations: [DashboardComponent],
})
export class DashboardModule {}
