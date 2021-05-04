import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { getWalletGroupIdentifier, IWalletGroup } from 'app/modules/wallet-group/wallet-group.model';
import { Observable } from 'rxjs';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';

export type EntityResponseType = HttpResponse<IWalletGroup>;
export type EntityArrayResponseType = HttpResponse<IWalletGroup[]>;

@Injectable({ providedIn: 'root' })
export class WalletGroupService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/wallet-groups');

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(walletGroup: IWalletGroup): Observable<EntityResponseType> {
    return this.http.post<IWalletGroup>(this.resourceUrl, walletGroup, { observe: 'response' });
  }

  update(walletGroup: IWalletGroup): Observable<EntityResponseType> {
    return this.http.put<IWalletGroup>(`${this.resourceUrl}/${getWalletGroupIdentifier(walletGroup) as number}`, walletGroup, {
      observe: 'response',
    });
  }

  partialUpdate(walletGroup: IWalletGroup): Observable<EntityResponseType> {
    return this.http.patch<IWalletGroup>(`${this.resourceUrl}/${getWalletGroupIdentifier(walletGroup) as number}`, walletGroup, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IWalletGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IWalletGroup[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addWalletGroupToCollectionIfMissing(
    walletGroupCollection: IWalletGroup[],
    ...walletGroupsToCheck: (IWalletGroup | null | undefined)[]
  ): IWalletGroup[] {
    const walletGroups: IWalletGroup[] = walletGroupsToCheck.filter(isPresent);
    if (walletGroups.length > 0) {
      const walletGroupCollectionIdentifiers = walletGroupCollection.map(walletGroupItem => getWalletGroupIdentifier(walletGroupItem)!);
      const walletGroupsToAdd = walletGroups.filter(walletGroupItem => {
        const walletGroupIdentifier = getWalletGroupIdentifier(walletGroupItem);
        if (walletGroupIdentifier == null || walletGroupCollectionIdentifiers.includes(walletGroupIdentifier)) {
          return false;
        }
        walletGroupCollectionIdentifiers.push(walletGroupIdentifier);
        return true;
      });
      return [...walletGroupsToAdd, ...walletGroupCollection];
    }
    return walletGroupCollection;
  }
}
