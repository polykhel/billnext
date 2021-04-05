import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IWallet, Wallet } from '../wallet.model';
import { WalletService } from '../service/wallet.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-wallet-update',
  templateUrl: './wallet-update.component.html',
})
export class WalletUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [null, [Validators.required]],
    walletGroup: [null, [Validators.required]],
    name: [null, [Validators.required]],
    amount: [null, [Validators.required]],
    currency: [],
    remarks: [],
    user: [null, Validators.required],
  });

  constructor(
    protected walletService: WalletService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ wallet }) => {
      this.updateForm(wallet);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const wallet = this.createFromForm();
    if (wallet.id !== undefined) {
      this.subscribeToSaveResponse(this.walletService.update(wallet));
    } else {
      this.subscribeToSaveResponse(this.walletService.create(wallet));
    }
  }

  trackUserById(index: number, item: IUser): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWallet>>): void {
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

  protected updateForm(wallet: IWallet): void {
    this.editForm.patchValue({
      id: wallet.id,
      walletGroup: wallet.walletGroup,
      name: wallet.name,
      amount: wallet.amount,
      currency: wallet.currency,
      remarks: wallet.remarks,
      user: wallet.user,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, wallet.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IWallet {
    return {
      ...new Wallet(),
      id: this.editForm.get(['id'])!.value,
      walletGroup: this.editForm.get(['walletGroup'])!.value,
      name: this.editForm.get(['name'])!.value,
      amount: this.editForm.get(['amount'])!.value,
      currency: this.editForm.get(['currency'])!.value,
      remarks: this.editForm.get(['remarks'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }
}
