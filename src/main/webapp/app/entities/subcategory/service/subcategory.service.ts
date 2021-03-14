import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/core/request/request-util';
import { ISubcategory } from '../subcategory.model';

type EntityResponseType = HttpResponse<ISubcategory>;
type EntityArrayResponseType = HttpResponse<ISubcategory[]>;

@Injectable({ providedIn: 'root' })
export class SubcategoryService {
  public resourceUrl = SERVER_API_URL + 'api/subcategories';

  constructor(protected http: HttpClient) {}

  create(subcategory: ISubcategory): Observable<EntityResponseType> {
    return this.http.post<ISubcategory>(this.resourceUrl, subcategory, { observe: 'response' });
  }

  update(subcategory: ISubcategory): Observable<EntityResponseType> {
    return this.http.put<ISubcategory>(this.resourceUrl, subcategory, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISubcategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISubcategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
