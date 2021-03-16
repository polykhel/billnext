import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ISubcategory, Subcategory } from '../subcategory.model';
import { SubcategoryService } from '../service/subcategory.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';

@Component({
  selector: 'app-subcategory-update',
  templateUrl: './subcategory-update.component.html',
})
export class SubcategoryUpdateComponent implements OnInit {
  isSaving = false;
  categories: ICategory[] = [];

  editForm = this.fb.group({
    id: [null, [Validators.required]],
    name: [null, [Validators.required]],
    category: [null, Validators.required],
  });

  constructor(
    protected subcategoryService: SubcategoryService,
    protected categoryService: CategoryService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ subcategory }) => {
      this.updateForm(subcategory);

      this.categoryService.query().subscribe((res: HttpResponse<ICategory[]>) => (this.categories = res.body ?? []));
    });
  }

  updateForm(subcategory: ISubcategory): void {
    this.editForm.patchValue({
      id: subcategory.id,
      name: subcategory.name,
      category: subcategory.category,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const subcategory = this.createFromForm();
    if (subcategory.id !== undefined) {
      this.subscribeToSaveResponse(this.subcategoryService.update(subcategory));
    } else {
      this.subscribeToSaveResponse(this.subcategoryService.create(subcategory));
    }
  }

  private createFromForm(): ISubcategory {
    return {
      ...new Subcategory(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      category: this.editForm.get(['category'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISubcategory>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackCategoryById(index: number, item: ICategory): number {
    return item.id!;
  }
}
