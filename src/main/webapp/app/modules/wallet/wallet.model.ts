import { IWalletGroup } from 'app/modules/wallet-group/wallet-group.model';

export interface IWallet {
  id?: number;
  name?: string;
  amount?: number;
  currency?: string | null;
  remarks?: string | null;
  walletGroup?: IWalletGroup;
}

export class Wallet implements IWallet {
  constructor(
    public id?: number,
    public name?: string,
    public amount?: number,
    public currency?: string | null,
    public remarks?: string | null,
    public walletGroup?: IWalletGroup
  ) {}
}

export function getWalletIdentifier(wallet: IWallet): number | undefined {
  return wallet.id;
}
