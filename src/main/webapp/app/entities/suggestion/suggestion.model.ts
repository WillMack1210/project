import dayjs from 'dayjs/esm';
import { IFindTime } from 'app/entities/find-time/find-time.model';

export interface ISuggestion {
  id: number;
  suggestedStart?: dayjs.Dayjs | null;
  suggestedEnd?: dayjs.Dayjs | null;
  findTimes?: Pick<IFindTime, 'id'>[] | null;
}

export type NewSuggestion = Omit<ISuggestion, 'id'> & { id: null };
