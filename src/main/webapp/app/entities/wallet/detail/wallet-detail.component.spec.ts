import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { Wallet } from '../wallet.model';

import { WalletDetailComponent } from './wallet-detail.component';

describe('Component Tests', () => {
  describe('Wallet Management Detail Component', () => {
    let comp: WalletDetailComponent;
    let fixture: ComponentFixture<WalletDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [WalletDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ wallet: new Wallet(123) }) },
          },
        ],
      })
        .overrideTemplate(WalletDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(WalletDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load wallet on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.wallet).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
