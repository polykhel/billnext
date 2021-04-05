import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IWallet } from '../wallet.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { WalletService } from '../service/wallet.service';
import { WalletDeleteDialogComponent } from '../delete/wallet-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'app-wallet',
  templateUrl: './wallet.component.html',
})
export class WalletComponent implements OnInit {
  wallets: IWallet[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected walletService: WalletService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.wallets = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.walletService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IWallet[]>) => {
          this.isLoading = false;
          this.paginateWallets(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.wallets = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IWallet): number {
    return item.id!;
  }

  delete(wallet: IWallet): void {
    const modalRef = this.modalService.open(WalletDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.wallet = wallet;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateWallets(data: IWallet[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.wallets.push(d);
      }
    }
  }
}
