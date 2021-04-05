import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

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
  users: IUser[] = [];

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
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ wallet }) => {
      this.updateForm(wallet);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body ?? []));
    });
  }

  updateForm(wallet: IWallet): void {
    this.editForm.patchValue({
      id: wallet.id,
      walletGroup: wallet.walletGroup,
      name: wallet.name,
      amount: wallet.amount,
      currency: wallet.currency,
      remarks: wallet.remarks,
      user: wallet.user,
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

  private createFromForm(): IWallet {
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWallet>>): void {
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

  trackUserById(index: number, item: IUser): string {
    return item.id;
  }
}
