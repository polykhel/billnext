import { NavigationService } from 'app/shared/navigation/navigation.service';
import { NavigationEnd, Router } from '@angular/router';
import { Location } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

jest.mock('@angular/common');

class MockRouter {
  public events = of(
    new NavigationEnd(0, 'http://localhost:4200/login', 'http://localhost:4200/login'),
    new NavigationEnd(1, 'http://localhost:4200/login', 'http://localhost:4200/login')
  );

  navigateByUrl = jest.fn().mockImplementation(() => new Promise(() => {}));
}

describe('Service Tests', () => {
  describe('Navigation Service', () => {
    let service: NavigationService;
    let location: Location;
    let router: Router;

    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [Location, { provide: Router, useClass: MockRouter }],
      });

      service = TestBed.inject(NavigationService);
      location = TestBed.inject(Location);
      router = TestBed.inject(Router);
    });

    describe('back', () => {
      it('should call location back if history is not empty', () => {
        service.back();

        expect(location.back).toHaveBeenCalled();
      });

      it('should call router navigateByUrl if history is empty', () => {
        service['history'] = [];
        service.back();

        expect(router.navigateByUrl).toHaveBeenCalled();
      });
    });
  });
});
