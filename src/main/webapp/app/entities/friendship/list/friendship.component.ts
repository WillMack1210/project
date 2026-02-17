import { Component, NgZone, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from '../../../core/auth/account.service';
import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { IFriendship } from '../friendship.model';
import { EntityArrayResponseType, FriendshipService } from '../service/friendship.service';
import { FriendshipDeleteDialogComponent } from '../delete/friendship-delete-dialog.component';
import { FriendshipExtendedService } from 'app/entities/friendship/service/friendship-extended.service';
import { IFriendshipStatus } from 'app/entities/friendship/friendship-status.model';
import { FriendStatus } from 'app/entities/enumerations/friend-status.model';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { parseClassNames } from '@fullcalendar/core/internal';

@Component({
  standalone: true,
  selector: 'jhi-friendship',
  templateUrl: './friendship.component.html',
  imports: [
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
export class FriendshipComponent implements OnInit {
  friendshipStatusMap: Record<number, IFriendshipStatus> = {};
  subscription: Subscription | null = null;
  friendships?: IFriendship[];
  isLoading = false;
  friends: IUserProfile[] = [];
  isAdmin: boolean;
  currentUserProfileId?: number | null = null;

  sortState = sortStateSignal({});

  protected readonly friendshipService = inject(FriendshipService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);
  protected userProfileService = inject(UserProfileService);
  protected friendshipExtendedService = inject(FriendshipExtendedService);

  constructor(
    private accountService: AccountService,
    private router: Router,
  ) {
    this.isAdmin = this.accountService.hasAnyAuthority('ROLE_ADMIN');
    this.router = router;
  }
  trackId = (item: IFriendship): number => this.friendshipService.getFriendshipIdentifier(item);

  ngOnInit(): void {
    this.getCurrentUser();
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)))
      .subscribe();
  }

  delete(friendship: IFriendship): void {
    const modalRef = this.modalService.open(FriendshipDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.friendship = friendship;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  loadFriends(profileId: number): void {
    this.friendshipExtendedService.getFriends(profileId).subscribe(friends => {
      this.friends = friends;
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  getCurrentUser(): void {
    this.accountService.identity().subscribe(account => {
      if (account?.login) {
        const queryObj: any = { eagerload: true };
        this.userProfileService.query(queryObj).subscribe(resp => {
          const profiles = resp.body ?? [];
          const myProfile = profiles.find(p => p.user?.login === account.login);
          this.currentUserProfileId = myProfile?.id ?? null;
          if (this.currentUserProfileId != null) {
            this.loadFriends(this.currentUserProfileId);
          }
        });
      }
    });
  }
  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.friendships = this.refineData(dataFromBody);
  }

  protected refineData(data: IFriendship[]): IFriendship[] {
    const filtered = data.filter(e => e.status === FriendStatus.ACCEPTED);
    const { predicate, order } = this.sortState();
    return predicate && order ? filtered.sort(this.sortService.startSort({ predicate, order })) : filtered;
  }

  protected fillComponentAttributesFromResponseBody(data: IFriendship[] | null): IFriendship[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.friendshipService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
