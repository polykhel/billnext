import * as dayjs from 'dayjs';
import { IUser } from 'app/entities/user/user.model';
import { IWallet } from 'app/entities/wallet/wallet.model';
import { ICategory } from 'app/entities/category/category.model';
import { ActivityType } from 'app/entities/enumerations/activity-type.model';

export interface IActivity {
  id?: number;
  date?: dayjs.Dayjs;
  amount?: number;
  remarks?: string | null;
  type?: ActivityType;
  user?: IUser;
  wallet?: IWallet;
  category?: ICategory;
}

export class Activity implements IActivity {
  constructor(
    public id?: number,
    public date?: dayjs.Dayjs,
    public amount?: number,
    public remarks?: string | null,
    public type?: ActivityType,
    public user?: IUser,
    public wallet?: IWallet,
    public category?: ICategory
  ) {}
}
