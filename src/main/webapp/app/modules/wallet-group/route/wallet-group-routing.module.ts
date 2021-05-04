import { RouterModule, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { WalletGroupDetailComponent } from 'app/modules/wallet-group/detail/wallet-group-detail.component';
import { WalletGroupRoutingResolveService } from 'app/modules/wallet-group/route/wallet-group-routing-resolve.service';

const walletGroupRoute: Routes = [
  {
    path: '',
    // component: WalletG,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: WalletGroupDetailComponent,
    resolve: {
      walletGroup: WalletGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];
import { NgModule } from '@angular/core';

@NgModule({
  imports: [RouterModule.forChild(walletGroupRoute)],
  exports: [RouterModule],
})
export class WalletGroupRoutingModule {}
