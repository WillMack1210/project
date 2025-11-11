import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { FriendStatus } from 'app/entities/enumerations/friend-status.model';

export interface IFriendship {
  id: number;
  status?: keyof typeof FriendStatus | null;
  user?: Pick<IUserProfile, 'id'> | null;
  friend?: Pick<IUserProfile, 'id'> | null;
}

export type NewFriendship = Omit<IFriendship, 'id'> & { id: null };
