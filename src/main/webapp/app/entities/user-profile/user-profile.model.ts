import { IUser } from 'app/entities/user/user.model';
import { IEvent } from 'app/entities/event/event.model';
import { IFindTime } from 'app/entities/find-time/find-time.model';

export interface IUserProfile {
  id: number;
  username?: string | null;
  fullName?: string | null;
  profilePicture?: string | null;
  profilePictureContentType?: string | null;
  settings?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  sharedEvents?: Pick<IEvent, 'id'>[] | null;
  findTimes?: Pick<IFindTime, 'id'>[] | null;
}

export type NewUserProfile = Omit<IUserProfile, 'id'> & { id: null };
