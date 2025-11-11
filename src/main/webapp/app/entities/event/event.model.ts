import dayjs from 'dayjs/esm';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { PrivacyStatus } from 'app/entities/enumerations/privacy-status.model';

export interface IEvent {
  id: number;
  title?: string | null;
  description?: string | null;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  location?: string | null;
  privacy?: keyof typeof PrivacyStatus | null;
  participants?: Pick<IUserProfile, 'id'>[] | null;
  owner?: Pick<IUserProfile, 'id'> | null;
}

export type NewEvent = Omit<IEvent, 'id'> & { id: null };
