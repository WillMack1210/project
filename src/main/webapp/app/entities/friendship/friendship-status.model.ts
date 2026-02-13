export interface IFriendshipStatus {
  userProfileId: number;
  friendshipId: number;
  status: 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'NONE';
  isRequester: boolean;
}
