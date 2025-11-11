import dayjs from 'dayjs/esm';

import { IEvent, NewEvent } from './event.model';

export const sampleWithRequiredData: IEvent = {
  id: 24079,
  title: 'scared only',
  startTime: dayjs('2025-11-10T09:22'),
  endTime: dayjs('2025-11-10T08:28'),
  privacy: 'PRIVATE',
};

export const sampleWithPartialData: IEvent = {
  id: 10392,
  title: 'general extricate below',
  startTime: dayjs('2025-11-10T04:24'),
  endTime: dayjs('2025-11-09T23:09'),
  location: 'yahoo reboot',
  privacy: 'PRIVATE',
};

export const sampleWithFullData: IEvent = {
  id: 20536,
  title: 'versus uh-huh',
  description: '../fake-data/blob/hipster.txt',
  startTime: dayjs('2025-11-10T05:35'),
  endTime: dayjs('2025-11-10T05:27'),
  location: 'anneal phooey equally',
  privacy: 'PUBLIC',
};

export const sampleWithNewData: NewEvent = {
  title: 'sheepishly live creator',
  startTime: dayjs('2025-11-10T11:23'),
  endTime: dayjs('2025-11-10T15:12'),
  privacy: 'PRIVATE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
