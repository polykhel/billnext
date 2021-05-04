import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';

import { IWalletGroup, WalletGroup } from '../wallet-group.model';
import { WalletGroupService } from 'app/modules/wallet-group/service/wallet-group.service';
import { mergeMap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class WalletGroupRoutingResolveService implements Resolve<IWalletGroup> {
  constructor(private service: WalletGroupService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IWalletGroup> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((walletGroup: HttpResponse<WalletGroup>) => {
          if (walletGroup.body) {
            return of(walletGroup.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new WalletGroup());
  }
}
