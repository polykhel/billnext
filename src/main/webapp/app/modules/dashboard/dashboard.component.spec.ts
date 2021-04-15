import { DashboardComponent } from './dashboard.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { addMonths, subMonths } from 'date-fns';

describe('Component Tests', () => {
  describe('Dashboard Component', () => {
    let comp: DashboardComponent;
    let fixture: ComponentFixture<DashboardComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [DashboardComponent],
      })
        .overrideTemplate(DashboardComponent, '')
        .compileComponents();
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(DashboardComponent);
      comp = fixture.componentInstance;
    });

    it('should create', () => {
      expect(comp).toBeTruthy();
    });

    describe('goToPreviousMonth', () => {
      it('should subtract one month from the selected date', () => {
        comp.goToPreviousMonth();

        const oneMonthBefore = subMonths(new Date(), 1);
        expect(comp.selectedDate.getDate()).toEqual(oneMonthBefore.getDate());
        expect(comp.selectedDate.getMonth()).toEqual(oneMonthBefore.getMonth());
        expect(comp.selectedDate.getFullYear()).toEqual(oneMonthBefore.getFullYear());
      });
    });

    describe('goToNextMonth', () => {
      it('should add one month to the selected date', () => {
        comp.goToNextMonth();

        const oneMonthAfter = addMonths(new Date(), 1);
        expect(comp.selectedDate.getDate()).toEqual(oneMonthAfter.getDate());
        expect(comp.selectedDate.getMonth()).toEqual(oneMonthAfter.getMonth());
        expect(comp.selectedDate.getFullYear()).toEqual(oneMonthAfter.getFullYear());
      });
    });
  });
});
