import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IActivity, Activity } from '../activity.model';
import { ActivityService } from '../service/activity.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IWallet } from 'app/entities/wallet/wallet.model';
import { WalletService } from 'app/entities/wallet/service/wallet.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';

@Component({
  selector: 'app-activity-update',
  templateUrl: './activity-update.component.html',
})
export class ActivityUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  walletsSharedCollection: IWallet[] = [];
  categoriesSharedCollection: ICategory[] = [];

  editForm = this.fb.group({
    id: [null, [Validators.required]],
    date: [null, [Validators.required]],
    amount: [null, [Validators.required]],
    remarks: [],
    type: [null, [Validators.required]],
    user: [null, Validators.required],
    wallet: [null, Validators.required],
    category: [null, Validators.required],
  });

  constructor(
    protected activityService: ActivityService,
    protected userService: UserService,
    protected walletService: WalletService,
    protected categoryService: CategoryService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ activity }) => {
      if (activity.id === undefined) {
        const today = dayjs().startOf('day');
        activity.date = today;
      }

      this.updateForm(activity);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const activity = this.createFromForm();
    if (activity.id !== undefined) {
      this.subscribeToSaveResponse(this.activityService.update(activity));
    } else {
      this.subscribeToSaveResponse(this.activityService.create(activity));
    }
  }

  trackUserById(index: number, item: IUser): string {
    return item.id!;
  }

  trackWalletById(index: number, item: IWallet): number {
    return item.id!;
  }

  trackCategoryById(index: number, item: ICategory): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActivity>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(activity: IActivity): void {
    this.editForm.patchValue({
      id: activity.id,
      date: activity.date ? activity.date.format(DATE_TIME_FORMAT) : null,
      amount: activity.amount,
      remarks: activity.remarks,
      type: activity.type,
      user: activity.user,
      wallet: activity.wallet,
      category: activity.category,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, activity.user);
    this.walletsSharedCollection = this.walletService.addWalletToCollectionIfMissing(this.walletsSharedCollection, activity.wallet);
    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing(
      this.categoriesSharedCollection,
      activity.category
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.walletService
      .query()
      .pipe(map((res: HttpResponse<IWallet[]>) => res.body ?? []))
      .pipe(map((wallets: IWallet[]) => this.walletService.addWalletToCollectionIfMissing(wallets, this.editForm.get('wallet')!.value)))
      .subscribe((wallets: IWallet[]) => (this.walletsSharedCollection = wallets));

    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing(categories, this.editForm.get('category')!.value)
        )
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));
  }

  protected createFromForm(): IActivity {
    return {
      ...new Activity(),
      id: this.editForm.get(['id'])!.value,
      date: this.editForm.get(['date'])!.value ? dayjs(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      amount: this.editForm.get(['amount'])!.value,
      remarks: this.editForm.get(['remarks'])!.value,
      type: this.editForm.get(['type'])!.value,
      user: this.editForm.get(['user'])!.value,
      wallet: this.editForm.get(['wallet'])!.value,
      category: this.editForm.get(['category'])!.value,
    };
  }
}
