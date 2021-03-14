import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { Subcategory } from '../subcategory.model';

import { SubcategoryDetailComponent } from './subcategory-detail.component';

describe('Component Tests', () => {
  describe('Subcategory Management Detail Component', () => {
    let comp: SubcategoryDetailComponent;
    let fixture: ComponentFixture<SubcategoryDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SubcategoryDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ subcategory: new Subcategory(123) }) },
          },
        ],
      })
        .overrideTemplate(SubcategoryDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SubcategoryDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load subcategory on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.subcategory).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
