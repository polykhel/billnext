import { WalletGroupDetailComponent } from 'app/modules/wallet-group/detail/wallet-group-detail.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

describe('Component Tests', () => {
  describe('WalletGroup Management Detail Component', () => {
    let comp: WalletGroupDetailComponent;
    let fixture: ComponentFixture<WalletGroupDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [WalletGroupDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ walletGroup: { id: 123 } }) },
          },
          TranslateService,
        ],
      })
        .overrideTemplate(WalletGroupDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(WalletGroupDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('should load walletGroup on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.walletGroup).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
