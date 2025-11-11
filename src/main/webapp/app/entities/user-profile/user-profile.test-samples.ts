import { IUserProfile, NewUserProfile } from './user-profile.model';

export const sampleWithRequiredData: IUserProfile = {
  id: 6065,
  username: 'longingly unnecessarily',
  fullName: 'giggle stingy mmm',
};

export const sampleWithPartialData: IUserProfile = {
  id: 9539,
  username: 'regularly',
  fullName: 'whereas regularly',
  profilePicture: '../fake-data/blob/hipster.png',
  profilePictureContentType: 'unknown',
};

export const sampleWithFullData: IUserProfile = {
  id: 10505,
  username: 'gosh hover nor',
  fullName: 'regarding fleck',
  profilePicture: '../fake-data/blob/hipster.png',
  profilePictureContentType: 'unknown',
  settings: 'readily selfish',
};

export const sampleWithNewData: NewUserProfile = {
  username: 'showy hm deed',
  fullName: 'generously coordination',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
