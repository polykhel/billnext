jest.mock('@angular/router');

import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { WalletGroupRoutingResolveService } from 'app/modules/wallet-group/route/wallet-group-routing-resolve.service';
import { WalletGroupService } from 'app/modules/wallet-group/service/wallet-group.service';
import { IWalletGroup, WalletGroup } from 'app/modules/wallet-group/wallet-group.model';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

describe('Service Tests', () => {
  describe('WalletGroup routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: WalletGroupRoutingResolveService;
    let service: WalletGroupService;
    let resultWalletGroup: IWalletGroup | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(WalletGroupRoutingResolveService);
      service = TestBed.inject(WalletGroupService);
      resultWalletGroup = undefined;
    });

    describe('resolve', () => {
      it('should return IWalletGroup returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultWalletGroup = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultWalletGroup).toEqual({ id: 123 });
      });

      it('should return new IWalletGroup if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultWalletGroup = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultWalletGroup).toEqual(new WalletGroup());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultWalletGroup = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultWalletGroup).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
