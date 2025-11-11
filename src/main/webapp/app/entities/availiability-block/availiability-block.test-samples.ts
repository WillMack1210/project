import dayjs from 'dayjs/esm';

import { IAvailiabilityBlock, NewAvailiabilityBlock } from './availiability-block.model';

export const sampleWithRequiredData: IAvailiabilityBlock = {
  id: 29810,
  startDateTime: dayjs('2025-11-09T18:00'),
  endDateTime: dayjs('2025-11-10T02:41'),
};

export const sampleWithPartialData: IAvailiabilityBlock = {
  id: 766,
  startDateTime: dayjs('2025-11-09T17:09'),
  endDateTime: dayjs('2025-11-10T13:30'),
};

export const sampleWithFullData: IAvailiabilityBlock = {
  id: 7032,
  startDateTime: dayjs('2025-11-10T11:12'),
  endDateTime: dayjs('2025-11-10T10:36'),
};

export const sampleWithNewData: NewAvailiabilityBlock = {
  startDateTime: dayjs('2025-11-09T23:11'),
  endDateTime: dayjs('2025-11-10T12:42'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
