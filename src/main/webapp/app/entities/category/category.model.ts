import { IUser } from 'app/entities/user/user.model';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityType } from 'app/entities/enumerations/activity-type.model';

export interface ICategory {
  id?: number;
  name?: string;
  type?: ActivityType | null;
  user?: IUser;
  activities?: IActivity[] | null;
}

export class Category implements ICategory {
  constructor(
    public id?: number,
    public name?: string,
    public type?: ActivityType | null,
    public user?: IUser,
    public activities?: IActivity[] | null
  ) {}
}
