import { Component, OnInit } from '@angular/core';
import { IUser } from 'app/modules/user/user.model';
import { FormBuilder, Validators } from '@angular/forms';
import { WalletGroupService } from 'app/modules/wallet-group/service/wallet-group.service';
import { UserService } from 'app/modules/user/user.service';
import { ActivatedRoute } from '@angular/router';
import { IWalletGroup, WalletGroup } from 'app/modules/wallet-group/wallet-group.model';
import { HttpResponse } from '@angular/common/http';
import { finalize, map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { NavigationService } from 'app/shared/navigation/navigation.service';

@Component({
  selector: 'app-wallet-group-update',
  templateUrl: './wallet-group-update.component.html',
})
export class WalletGroupUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    user: [null, Validators.required],
  });

  constructor(
    protected walletGroupService: WalletGroupService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected navigationService: NavigationService,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ walletGroup }) => {
      this.updateForm(walletGroup);

      this.loadRelationshipsOptions();
    });
  }

  save(): void {
    this.isSaving = true;
    const walletGroup = this.createFromForm();
    if (walletGroup.id !== undefined) {
      this.subscribeToSaveResponse(this.walletGroupService.update(walletGroup));
    } else {
      this.subscribeToSaveResponse(this.walletGroupService.create(walletGroup));
    }
  }

  trackUserById(index: number, item: IUser): string {
    return item.id;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWalletGroup>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.navigationService.back();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(walletGroup: IWalletGroup): void {
    this.editForm.patchValue({
      id: walletGroup.id,
      name: walletGroup.name,
      user: walletGroup.user,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, walletGroup.user);
  }

  protected createFromForm(): IWalletGroup {
    return {
      ...new WalletGroup(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }

  private loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
