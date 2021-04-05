jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IWallet, Wallet } from '../wallet.model';
import { WalletService } from '../service/wallet.service';

import { WalletRoutingResolveService } from './wallet-routing-resolve.service';

describe('Service Tests', () => {
  describe('Wallet routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: WalletRoutingResolveService;
    let service: WalletService;
    let resultWallet: IWallet | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(WalletRoutingResolveService);
      service = TestBed.inject(WalletService);
      resultWallet = undefined;
    });

    describe('resolve', () => {
      it('should return existing IWallet for existing id', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: new Wallet(id) })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultWallet = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultWallet).toEqual(new Wallet(123));
      });

      it('should return new IWallet if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultWallet = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultWallet).toEqual(new Wallet());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultWallet = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultWallet).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
