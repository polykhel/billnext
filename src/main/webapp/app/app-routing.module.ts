import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';
import { DEBUG_INFO_ENABLED } from './app.constants';
import { Authority } from './config/authority.constants';

import { UserRouteAccessService } from './core/auth/user-route-access.service';
import { DashboardComponent } from './modules/dashboard/dashboard.component';

const LAYOUT_ROUTES = [navbarRoute, ...errorRoute];

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'dashboard',
          data: {
            authorities: [Authority.USER],
          },
          canActivate: [UserRouteAccessService],
          component: DashboardComponent,
        },
        ...LAYOUT_ROUTES,
      ],
      { enableTracing: DEBUG_INFO_ENABLED }
    ),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
