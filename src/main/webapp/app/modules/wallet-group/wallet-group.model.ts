import { IUser } from 'app/modules/user/user.model';

export interface IWalletGroup {
  id?: number;
  name?: string;
  user?: IUser;
}

export class WalletGroup implements IWalletGroup {
  constructor(public id?: number, public name?: string, public user?: IUser) {}
}

export function getWalletGroupIdentifier(walletGroup: IWalletGroup): number | undefined {
  return walletGroup.id;
}
