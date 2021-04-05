jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SubcategoryService } from '../service/subcategory.service';
import { ISubcategory, Subcategory } from '../subcategory.model';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';

import { SubcategoryUpdateComponent } from './subcategory-update.component';

describe('Component Tests', () => {
  describe('Subcategory Management Update Component', () => {
    let comp: SubcategoryUpdateComponent;
    let fixture: ComponentFixture<SubcategoryUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let subcategoryService: SubcategoryService;
    let categoryService: CategoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SubcategoryUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SubcategoryUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SubcategoryUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      subcategoryService = TestBed.inject(SubcategoryService);
      categoryService = TestBed.inject(CategoryService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Category query and add missing value', () => {
        const subcategory: ISubcategory = { id: 456 };
        const category: ICategory = { id: 75530 };
        subcategory.category = category;

        const categoryCollection: ICategory[] = [{ id: 82897 }];
        spyOn(categoryService, 'query').and.returnValue(of(new HttpResponse({ body: categoryCollection })));
        const additionalCategories = [category];
        const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
        spyOn(categoryService, 'addCategoryToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ subcategory });
        comp.ngOnInit();

        expect(categoryService.query).toHaveBeenCalled();
        expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(categoryCollection, ...additionalCategories);
        expect(comp.categoriesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const subcategory: ISubcategory = { id: 456 };
        const category: ICategory = { id: 8745 };
        subcategory.category = category;

        activatedRoute.data = of({ subcategory });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(subcategory));
        expect(comp.categoriesSharedCollection).toContain(category);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const subcategory = { id: 123 };
        spyOn(subcategoryService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ subcategory });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: subcategory }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(subcategoryService.update).toHaveBeenCalledWith(subcategory);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const subcategory = new Subcategory();
        spyOn(subcategoryService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ subcategory });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: subcategory }));
        saveSubject.complete();

        // THEN
        expect(subcategoryService.create).toHaveBeenCalledWith(subcategory);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const subcategory = { id: 123 };
        spyOn(subcategoryService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ subcategory });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(subcategoryService.update).toHaveBeenCalledWith(subcategory);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
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
