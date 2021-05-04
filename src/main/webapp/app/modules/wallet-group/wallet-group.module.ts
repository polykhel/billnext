import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WalletGroupDetailComponent } from 'app/modules/wallet-group/detail/wallet-group-detail.component';
import { WalletGroupRoutingModule } from 'app/modules/wallet-group/route/wallet-group-routing.module';

@NgModule({
  imports: [SharedModule, WalletGroupRoutingModule],
  declarations: [WalletGroupDetailComponent],
  providers: [],
})
export class WalletGroupModule {}
