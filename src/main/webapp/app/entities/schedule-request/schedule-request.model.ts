import dayjs from 'dayjs/esm';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { ScheduleIntensity } from 'app/entities/enumerations/schedule-intensity.model';

export interface IScheduleRequest {
  id: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  scheduleDescription?: string | null;
  intensity?: keyof typeof ScheduleIntensity | null;
  user?: Pick<IUserProfile, 'id'> | null;
}

export type NewScheduleRequest = Omit<IScheduleRequest, 'id'> & { id: null };
