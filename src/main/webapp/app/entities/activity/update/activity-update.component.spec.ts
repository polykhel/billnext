jest.mock('@angular/router');

import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ActivityService } from '../service/activity.service';
import { Activity } from '../activity.model';
import { User } from 'app/entities/user/user.model';
import { Wallet } from 'app/entities/wallet/wallet.model';
import { Category } from 'app/entities/category/category.model';

import { ActivityUpdateComponent } from './activity-update.component';

describe('Component Tests', () => {
  describe('Activity Management Update Component', () => {
    let comp: ActivityUpdateComponent;
    let fixture: ComponentFixture<ActivityUpdateComponent>;
    let service: ActivityService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ActivityUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ActivityUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ActivityUpdateComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ActivityService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Activity(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Activity();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = new User('123', 'user');
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackWalletById', () => {
        it('Should return tracked Wallet primary key', () => {
          const entity = new Wallet(123);
          const trackResult = comp.trackWalletById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackCategoryById', () => {
        it('Should return tracked Category primary key', () => {
          const entity = new Category(123);
          const trackResult = comp.trackCategoryById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
