import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WalletGroupDetailComponent } from './detail/wallet-group-detail.component';
import { WalletGroupRoutingModule } from './route/wallet-group-routing.module';
import { WalletGroupUpdateComponent } from './update/wallet-group-update.component';
import { WalletGroupComponent } from 'app/modules/wallet-group/list/wallet-group.component';

@NgModule({
  imports: [SharedModule, WalletGroupRoutingModule],
  declarations: [WalletGroupDetailComponent, WalletGroupUpdateComponent, WalletGroupComponent],
  providers: [],
})
export class WalletGroupModule {}
