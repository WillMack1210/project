import dayjs from 'dayjs/esm';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';

export interface IAvailiabilityBlock {
  id: number;
  startDateTime?: dayjs.Dayjs | null;
  endDateTime?: dayjs.Dayjs | null;
  user?: Pick<IUserProfile, 'id'> | null;
}

export type NewAvailiabilityBlock = Omit<IAvailiabilityBlock, 'id'> & { id: null };
