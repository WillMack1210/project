import { Component, NgZone, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Observable, Subscription, map, switchMap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, sortStateSignal } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { IFindTime } from '../find-time.model';
import { FindTimeService } from '../service/find-time.service';
import { ITimeSlot } from '../service/find-time.service';
import { AccountService } from 'app/core/auth/account.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { FriendshipExtendedService } from 'app/entities/friendship/service/friendship-extended.service';
@Component({
  standalone: true,
  selector: 'jhi-find-time',
  templateUrl: './find-time.component.html',
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
  ],
})
export class FindTimeComponent implements OnInit {
  subscription: Subscription | null = null;
  findTimes?: IFindTime[];
  isLoading = false;
  freeSlots: ITimeSlot[] = [];
  currentUserProfileId?: number;
  selectedFriendId?: number;
  requestStart?: string;
  requestEnd?: string;
  friends: IUserProfile[] = [];
  hasSearched = false;

  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly findTimeService = inject(FindTimeService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected readonly accountService = inject(AccountService);
  protected readonly userProfileService = inject(UserProfileService);
  protected readonly friendshipExtendedService = inject(FriendshipExtendedService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IFindTime): number => this.findTimeService.getFindTimeIdentifier(item);

  ngOnInit(): void {
    this.getCurrentUser().subscribe(profileId => {
      this.currentUserProfileId = profileId;
      this.loadFriends(profileId);
    });
  }
  loadFriends(profileId: number): void {
    this.friendshipExtendedService.getFriends(profileId).subscribe(friends => {
      this.friends = friends;
    });
  }

  getCurrentUser(): Observable<number> {
    return this.accountService.identity().pipe(
      switchMap(account => {
        if (!account?.login) {
          throw new Error('No logged in account');
        }

        return this.userProfileService.query({ eagerload: true }).pipe(
          map(resp => {
            const profiles = resp.body ?? [];
            const myProfile = profiles.find(p => p.user?.login === account.login);

            if (!myProfile?.id) {
              throw new Error('Profile not found for user');
            }

            return myProfile.id;
          }),
        );
      }),
    );
  }

  findCommonSlots(): void {
    if (!this.selectedFriendId || !this.requestStart || !this.requestEnd) {
      throw new Error('Something hasnt veen filled in');
    }
    this.freeSlots = [];
    this.hasSearched = false;
    const startISO = new Date(this.requestStart).toISOString();
    const endISO = new Date(this.requestEnd).toISOString();
    this.findTimeService.findCommonFreeSlots(this.currentUserProfileId!, this.selectedFriendId, startISO, endISO).subscribe({
      next: slots => {
        this.freeSlots = slots;
        this.hasSearched = true;
      },
      error: err => {
        console.error('Error fetching common free slots', err);
        this.hasSearched = true;
      },
    });
  }
}
