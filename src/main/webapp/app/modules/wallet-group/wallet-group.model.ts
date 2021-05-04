import { IUser } from 'app/modules/user/user.model';
import { IWallet } from 'app/modules/wallet/wallet.model';

export interface IWalletGroup {
  id?: number;
  name?: string;
  user?: IUser;
  wallets?: IWallet[] | null;
}

export class WalletGroup implements IWalletGroup {
  constructor(public id?: number, public name?: string, public user?: IUser) {}
}

export function getWalletGroupIdentifier(walletGroup: IWalletGroup): number | undefined {
  return walletGroup.id;
}
