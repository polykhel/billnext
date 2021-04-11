import { DashboardComponent } from './dashboard.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';

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
  });
});
