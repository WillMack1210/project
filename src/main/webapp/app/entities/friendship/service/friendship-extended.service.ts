import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IFriendshipStatus } from '../friendship-status.model';

@Injectable({
  providedIn: 'root',
})
export class FriendshipExtendedService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/friendships');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  sendFriendRequest(addresseeId: number): Observable<unknown> {
    return this.http.post<unknown>(`${this.resourceUrl}/request/${addresseeId}`, {});
  }
  acceptRequest(friendshipId: number): Observable<unknown> {
    return this.http.put<unknown>(`${this.resourceUrl}/accept/${friendshipId}`, {});
  }
  declineRequest(friendshipId: number): Observable<unknown> {
    return this.http.put<unknown>(`${this.resourceUrl}/decline/${friendshipId}`, {});
  }
  removeFriend(friendshipId: number): Observable<unknown> {
    return this.http.delete<unknown>(`${this.resourceUrl}/${friendshipId}`);
  }
  getFriendshipStatusesForCurrentUser(): Observable<IFriendshipStatus[]> {
    return this.http.get<IFriendshipStatus[]>(`${this.resourceUrl}/status`);
  }
}
