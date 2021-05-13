import { RouterModule, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { WalletGroupDetailComponent } from 'app/modules/wallet-group/detail/wallet-group-detail.component';
import { WalletGroupRoutingResolveService } from 'app/modules/wallet-group/route/wallet-group-routing-resolve.service';
import { NgModule } from '@angular/core';
import { WalletGroupUpdateComponent } from 'app/modules/wallet-group/update/wallet-group-update.component';
import { WalletGroupComponent } from 'app/modules/wallet-group/list/wallet-group.component';

const walletGroupRoute: Routes = [
  {
    path: '',
    component: WalletGroupComponent,
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
  {
    path: 'new',
    component: WalletGroupUpdateComponent,
    resolve: {
      walletGroup: WalletGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: WalletGroupUpdateComponent,
    resolve: {
      walletGroup: WalletGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];
@NgModule({
  imports: [RouterModule.forChild(walletGroupRoute)],
  exports: [RouterModule],
})
export class WalletGroupRoutingModule {}
