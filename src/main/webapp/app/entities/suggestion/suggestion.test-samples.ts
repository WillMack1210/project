import dayjs from 'dayjs/esm';

import { ISuggestion, NewSuggestion } from './suggestion.model';

export const sampleWithRequiredData: ISuggestion = {
  id: 4788,
  suggestedStart: dayjs('2025-11-10T10:47'),
  suggestedEnd: dayjs('2025-11-09T17:44'),
};

export const sampleWithPartialData: ISuggestion = {
  id: 19343,
  suggestedStart: dayjs('2025-11-09T17:52'),
  suggestedEnd: dayjs('2025-11-10T13:05'),
};

export const sampleWithFullData: ISuggestion = {
  id: 3186,
  suggestedStart: dayjs('2025-11-10T06:14'),
  suggestedEnd: dayjs('2025-11-09T23:26'),
};

export const sampleWithNewData: NewSuggestion = {
  suggestedStart: dayjs('2025-11-10T12:15'),
  suggestedEnd: dayjs('2025-11-10T09:59'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
