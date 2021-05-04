import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IWalletGroup, WalletGroup } from '../wallet-group.model';

import { WalletGroupService } from './wallet-group.service';

describe('Service Tests', () => {
  describe('WalletGroup Service', () => {
    let service: WalletGroupService;
    let httpMock: HttpTestingController;
    let elemDefault: IWalletGroup;
    let expectedResult: IWalletGroup | IWalletGroup[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(WalletGroupService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a WalletGroup', () => {
        const returnFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnFromService);

        service.create(new WalletGroup()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a WalletGroup', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a WalletGroup', () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
          },
          new WalletGroup()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of WalletGroup', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a WalletGroup', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addWalletGroupToCollectionIfMissing', () => {
        it('should add a WalletGroup to an empty array', () => {
          const walletGroup: IWalletGroup = { id: 123 };
          expectedResult = service.addWalletGroupToCollectionIfMissing([], walletGroup);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(walletGroup);
        });

        it('should not add a WalletGroup to an array that contains it', () => {
          const walletGroup: IWalletGroup = { id: 123 };
          const walletGroupCollection: IWalletGroup[] = [
            {
              ...walletGroup,
            },
            { id: 456 },
          ];
          expectedResult = service.addWalletGroupToCollectionIfMissing(walletGroupCollection, walletGroup);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a WalletGroup to an array that doesn't contain it", () => {
          const walletGroup: IWalletGroup = { id: 123 };
          const walletGroupCollection: IWalletGroup[] = [{ id: 456 }];
          expectedResult = service.addWalletGroupToCollectionIfMissing(walletGroupCollection, walletGroup);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(walletGroup);
        });

        it('should add only unique WalletGroup to an array', () => {
          const walletGroupArray: IWalletGroup[] = [{ id: 123 }, { id: 456 }, { id: 62928 }];
          const walletGroupCollection: IWalletGroup[] = [{ id: 123 }];
          expectedResult = service.addWalletGroupToCollectionIfMissing(walletGroupCollection, ...walletGroupArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const walletGroup: IWalletGroup = { id: 123 };
          const walletGroup2: IWalletGroup = { id: 456 };
          expectedResult = service.addWalletGroupToCollectionIfMissing([], walletGroup, walletGroup2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(walletGroup);
          expect(expectedResult).toContain(walletGroup2);
        });

        it('should accept null and undefined values', () => {
          const walletGroup: IWalletGroup = { id: 123 };
          expectedResult = service.addWalletGroupToCollectionIfMissing([], null, walletGroup, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(walletGroup);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
