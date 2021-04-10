import { Route } from '@angular/router';
import { DashboardComponent } from 'app/modules/dashboard/dashboard.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

export const dashboardRoute: Route = {
  path: '',
  component: DashboardComponent,
  canActivate: [UserRouteAccessService],
};
