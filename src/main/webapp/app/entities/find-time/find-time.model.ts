import dayjs from 'dayjs/esm';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { ISuggestion } from 'app/entities/suggestion/suggestion.model';

export interface IFindTime {
  id: number;
  requestStart?: dayjs.Dayjs | null;
  requestEnd?: dayjs.Dayjs | null;
  length?: number | null;
  requester?: Pick<IUserProfile, 'id'> | null;
  participants?: Pick<IUserProfile, 'id'>[] | null;
  suggestions?: Pick<ISuggestion, 'id'>[] | null;
}

export type NewFindTime = Omit<IFindTime, 'id'> & { id: null };
