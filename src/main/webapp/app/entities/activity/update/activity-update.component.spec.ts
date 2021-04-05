jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ActivityService } from '../service/activity.service';
import { IActivity, Activity } from '../activity.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IWallet } from 'app/entities/wallet/wallet.model';
import { WalletService } from 'app/entities/wallet/service/wallet.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';

import { ActivityUpdateComponent } from './activity-update.component';

describe('Component Tests', () => {
  describe('Activity Management Update Component', () => {
    let comp: ActivityUpdateComponent;
    let fixture: ComponentFixture<ActivityUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let activityService: ActivityService;
    let userService: UserService;
    let walletService: WalletService;
    let categoryService: CategoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ActivityUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ActivityUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ActivityUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      activityService = TestBed.inject(ActivityService);
      userService = TestBed.inject(UserService);
      walletService = TestBed.inject(WalletService);
      categoryService = TestBed.inject(CategoryService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const activity: IActivity = { id: 456 };
        const user: IUser = { id: 'Ohio Facilitator Rwanda' };
        activity.user = user;

        const userCollection: IUser[] = [{ id: 'Wooden' }];
        spyOn(userService, 'query').and.returnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        spyOn(userService, 'addUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Wallet query and add missing value', () => {
        const activity: IActivity = { id: 456 };
        const wallet: IWallet = { id: 86424 };
        activity.wallet = wallet;

        const walletCollection: IWallet[] = [{ id: 83956 }];
        spyOn(walletService, 'query').and.returnValue(of(new HttpResponse({ body: walletCollection })));
        const additionalWallets = [wallet];
        const expectedCollection: IWallet[] = [...additionalWallets, ...walletCollection];
        spyOn(walletService, 'addWalletToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        expect(walletService.query).toHaveBeenCalled();
        expect(walletService.addWalletToCollectionIfMissing).toHaveBeenCalledWith(walletCollection, ...additionalWallets);
        expect(comp.walletsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Category query and add missing value', () => {
        const activity: IActivity = { id: 456 };
        const category: ICategory = { id: 75834 };
        activity.category = category;

        const categoryCollection: ICategory[] = [{ id: 24965 }];
        spyOn(categoryService, 'query').and.returnValue(of(new HttpResponse({ body: categoryCollection })));
        const additionalCategories = [category];
        const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
        spyOn(categoryService, 'addCategoryToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        expect(categoryService.query).toHaveBeenCalled();
        expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(categoryCollection, ...additionalCategories);
        expect(comp.categoriesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const activity: IActivity = { id: 456 };
        const user: IUser = { id: 'Shilling Islands Investor' };
        activity.user = user;
        const wallet: IWallet = { id: 98936 };
        activity.wallet = wallet;
        const category: ICategory = { id: 30057 };
        activity.category = category;

        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(activity));
        expect(comp.usersSharedCollection).toContain(user);
        expect(comp.walletsSharedCollection).toContain(wallet);
        expect(comp.categoriesSharedCollection).toContain(category);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const activity = { id: 123 };
        spyOn(activityService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: activity }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(activityService.update).toHaveBeenCalledWith(activity);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const activity = new Activity();
        spyOn(activityService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: activity }));
        saveSubject.complete();

        // THEN
        expect(activityService.create).toHaveBeenCalledWith(activity);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const activity = { id: 123 };
        spyOn(activityService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ activity });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(activityService.update).toHaveBeenCalledWith(activity);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 'ABC' };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackWalletById', () => {
        it('Should return tracked Wallet primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackWalletById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackCategoryById', () => {
        it('Should return tracked Category primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCategoryById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
