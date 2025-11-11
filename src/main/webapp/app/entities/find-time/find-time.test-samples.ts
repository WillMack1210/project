import dayjs from 'dayjs/esm';

import { IFindTime, NewFindTime } from './find-time.model';

export const sampleWithRequiredData: IFindTime = {
  id: 5501,
  requestStart: dayjs('2025-11-10T11:11'),
  requestEnd: dayjs('2025-11-10T16:16'),
  length: 7928,
};

export const sampleWithPartialData: IFindTime = {
  id: 18507,
  requestStart: dayjs('2025-11-10T10:11'),
  requestEnd: dayjs('2025-11-10T03:11'),
  length: 435,
};

export const sampleWithFullData: IFindTime = {
  id: 4572,
  requestStart: dayjs('2025-11-09T19:37'),
  requestEnd: dayjs('2025-11-09T17:12'),
  length: 1413,
};

export const sampleWithNewData: NewFindTime = {
  requestStart: dayjs('2025-11-09T18:14'),
  requestEnd: dayjs('2025-11-10T09:17'),
  length: 32384,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
