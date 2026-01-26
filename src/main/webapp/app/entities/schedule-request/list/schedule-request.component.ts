import { Component, NgZone, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { DataUtils } from 'app/core/util/data-util.service';
import { IScheduleRequest } from '../schedule-request.model';
import { EntityArrayResponseType, ScheduleRequestService } from '../service/schedule-request.service';
import { ScheduleRequestDeleteDialogComponent } from '../delete/schedule-request-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';

@Component({
  standalone: true,
  selector: 'jhi-schedule-request',
  templateUrl: './schedule-request.component.html',
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
export class ScheduleRequestComponent implements OnInit {
  subscription: Subscription | null = null;
  scheduleRequests?: IScheduleRequest[];
  isLoading = false;
  currentUserProfileId?: number | null = null;

  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly scheduleRequestService = inject(ScheduleRequestService);
  protected readonly accountService = inject(AccountService);
  protected readonly userProfileService = inject(UserProfileService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected dataUtils = inject(DataUtils);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IScheduleRequest): number => this.scheduleRequestService.getScheduleRequestIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)))
      .subscribe(() => {
        this.accountService.identity().subscribe(account => {
          // find user profile for current account
          if (account?.login) {
            const queryObj: any = { eagerload: true };
            this.userProfileService.query(queryObj).subscribe(resp => {
              const profiles = resp.body ?? [];
              const myProfile = profiles.find(p => p.user?.login === account.login);
              this.currentUserProfileId = myProfile?.id ?? null;
              this.load();
            });
          } else {
            this.currentUserProfileId = null;
            this.load();
          }
        });
      });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(scheduleRequest: IScheduleRequest): void {
    const modalRef = this.modalService.open(ScheduleRequestDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.scheduleRequest = scheduleRequest;
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

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.scheduleRequests = this.refineData(dataFromBody);
  }

  protected refineData(data: IScheduleRequest[]): IScheduleRequest[] {
    // filter events to only those owned by the current user's profile (if known)
    const filtered = this.currentUserProfileId != null ? data.filter(e => e.user?.id === this.currentUserProfileId) : data;
    const { predicate, order } = this.sortState();
    return predicate && order ? filtered.sort(this.sortService.startSort({ predicate, order })) : filtered;
  }

  protected fillComponentAttributesFromResponseBody(data: IScheduleRequest[] | null): IScheduleRequest[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.scheduleRequestService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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
