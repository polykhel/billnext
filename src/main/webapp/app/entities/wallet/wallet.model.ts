import { IUser } from 'app/entities/user/user.model';
import { IActivity } from 'app/entities/activity/activity.model';
import { WalletGroup } from 'app/entities/enumerations/wallet-group.model';

export interface IWallet {
  id?: number;
  walletGroup?: WalletGroup;
  name?: string;
  amount?: number;
  currency?: string | null;
  remarks?: string | null;
  user?: IUser;
  activities?: IActivity[] | null;
}

export class Wallet implements IWallet {
  constructor(
    public id?: number,
    public walletGroup?: WalletGroup,
    public name?: string,
    public amount?: number,
    public currency?: string | null,
    public remarks?: string | null,
    public user?: IUser,
    public activities?: IActivity[] | null
  ) {}
}
