import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';

import { IWalletGroup, WalletGroup } from '../wallet-group.model';

@Injectable({ providedIn: 'root' })
export class WalletGroupRoutingResolveService implements Resolve<IWalletGroup> {
  constructor(private httpClient: HttpClient) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IWalletGroup> | Observable<never> {
    return of(new WalletGroup());
  }
}
