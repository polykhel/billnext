import { Component, OnInit } from '@angular/core';
import { IWalletGroup } from 'app/modules/wallet-group/wallet-group.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-wallet-group-detail',
  templateUrl: './wallet-group-detail.component.html',
})
export class WalletGroupDetailComponent implements OnInit {
  walletGroup: IWalletGroup | null = null;
  count = 1;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ walletGroup }) => {
      this.walletGroup = walletGroup;
    });
  }
}
