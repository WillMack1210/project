import { IFriendship, NewFriendship } from './friendship.model';

export const sampleWithRequiredData: IFriendship = {
  id: 9885,
  status: 'DECLINED',
};

export const sampleWithPartialData: IFriendship = {
  id: 21812,
  status: 'PENDING',
};

export const sampleWithFullData: IFriendship = {
  id: 895,
  status: 'PENDING',
};

export const sampleWithNewData: NewFriendship = {
  status: 'PENDING',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
