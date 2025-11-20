import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { FriendStatus } from 'app/entities/enumerations/friend-status.model';

export interface IFriendship {
  id: number;
  status?: keyof typeof FriendStatus | null;
  requester?: Pick<IUserProfile, 'id' | 'username'> | null;
  addressee?: Pick<IUserProfile, 'id' | 'username'> | null;
}

export type NewFriendship = Omit<IFriendship, 'id'> & { id: null };
