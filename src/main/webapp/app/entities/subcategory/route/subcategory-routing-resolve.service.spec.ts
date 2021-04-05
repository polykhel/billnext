jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISubcategory, Subcategory } from '../subcategory.model';
import { SubcategoryService } from '../service/subcategory.service';

import { SubcategoryRoutingResolveService } from './subcategory-routing-resolve.service';

describe('Service Tests', () => {
  describe('Subcategory routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SubcategoryRoutingResolveService;
    let service: SubcategoryService;
    let resultSubcategory: ISubcategory | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SubcategoryRoutingResolveService);
      service = TestBed.inject(SubcategoryService);
      resultSubcategory = undefined;
    });

    describe('resolve', () => {
      it('should return ISubcategory returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSubcategory = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSubcategory).toEqual({ id: 123 });
      });

      it('should return new ISubcategory if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSubcategory = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSubcategory).toEqual(new Subcategory());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSubcategory = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSubcategory).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
