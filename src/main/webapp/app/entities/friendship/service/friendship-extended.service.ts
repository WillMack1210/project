import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IFriendshipStatus } from '../friendship-status.model';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';

@Injectable({
  providedIn: 'root',
})
export class FriendshipExtendedService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/friendships');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  sendFriendRequest(addresseeId: number): Observable<any> {
    return this.http.post<any>(`${this.resourceUrl}/request/${addresseeId}`, {});
  }
  acceptRequest(friendshipId: number): Observable<any> {
    return this.http.put<any>(`${this.resourceUrl}/accept/${friendshipId}`, {});
  }
  declineRequest(friendshipId: number): Observable<any> {
    return this.http.put<any>(`${this.resourceUrl}/decline/${friendshipId}`, {});
  }
  removeFriend(friendshipId: number): Observable<any> {
    return this.http.delete<any>(`${this.resourceUrl}/${friendshipId}`);
  }
  getFriendshipStatusesForCurrentUser(): Observable<IFriendshipStatus[]> {
    return this.http.get<IFriendshipStatus[]>(`${this.resourceUrl}/status`);
  }
  getFriends(userProfileId: number): Observable<IUserProfile[]> {
    return this.http.get<IUserProfile[]>(`${this.resourceUrl}/friends/${userProfileId}`);
  }
}
