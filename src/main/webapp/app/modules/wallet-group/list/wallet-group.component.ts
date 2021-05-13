import { Component, OnInit } from '@angular/core';
import { IWalletGroup } from 'app/modules/wallet-group/wallet-group.model';
import { WalletGroupService } from 'app/modules/wallet-group/service/wallet-group.service';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'app-wallet-group',
  templateUrl: './wallet-group.component.html',
  styleUrls: ['./wallet-group.component.less'],
})
export class WalletGroupComponent implements OnInit {
  walletGroups: IWalletGroup[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;
  userId: string | undefined;
  isReordering = false;

  constructor(
    protected walletGroupService: WalletGroupService,
    protected parseLinks: ParseLinks,
    protected accountService: AccountService
  ) {
    this.walletGroups = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'orderIndex';
    this.ascending = true;
  }

  loadAll(): void {
    if (this.userId) {
      this.isLoading = true;

      this.walletGroupService
        .query({
          page: this.page,
          size: this.itemsPerPage,
          sort: this.sort(),
          'userId.equals': this.userId,
        })
        .subscribe(
          (res: HttpResponse<IWalletGroup[]>) => {
            this.isLoading = false;
            this.paginateWalletGroups(res.body, res.headers);
          },
          () => {
            this.isLoading = false;
          }
        );
    }
  }

  reset(): void {
    this.page = 0;
    this.walletGroups = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      this.userId = account?.id;

      this.loadAll();
    });
  }

  trackId(index: number, item: IWalletGroup): number {
    return item.id!;
  }

  reorder(): void {
    this.isReordering = !this.isReordering;
  }

  drop(event: CdkDragDrop<string[]>): void {
    moveItemInArray(this.walletGroups, event.previousIndex, event.currentIndex);
  }

  /*  delete(walletGroup: IWalletGroup): void {
    // modal delete
  }*/

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateWalletGroups(data: IWalletGroup[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      this.walletGroups = data;
    }
  }
}
