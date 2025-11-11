import dayjs from 'dayjs/esm';

import { IScheduleRequest, NewScheduleRequest } from './schedule-request.model';

export const sampleWithRequiredData: IScheduleRequest = {
  id: 31956,
  startDate: dayjs('2025-11-10T09:10'),
  endDate: dayjs('2025-11-09T16:39'),
  scheduleDescription: '../fake-data/blob/hipster.txt',
  intensity: 'INTENSE',
};

export const sampleWithPartialData: IScheduleRequest = {
  id: 17053,
  startDate: dayjs('2025-11-10T08:52'),
  endDate: dayjs('2025-11-10T09:13'),
  scheduleDescription: '../fake-data/blob/hipster.txt',
  intensity: 'INTERMEDIATE',
};

export const sampleWithFullData: IScheduleRequest = {
  id: 17301,
  startDate: dayjs('2025-11-10T14:25'),
  endDate: dayjs('2025-11-09T22:10'),
  scheduleDescription: '../fake-data/blob/hipster.txt',
  intensity: 'INTENSE',
};

export const sampleWithNewData: NewScheduleRequest = {
  startDate: dayjs('2025-11-09T18:09'),
  endDate: dayjs('2025-11-10T13:06'),
  scheduleDescription: '../fake-data/blob/hipster.txt',
  intensity: 'INTERMEDIATE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
