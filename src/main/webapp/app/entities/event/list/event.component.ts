import { Component, NgZone, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { DataUtils } from 'app/core/util/data-util.service';
import { IEvent } from '../event.model';
import { EntityArrayResponseType, EventService } from '../service/event.service';
import { EventDeleteDialogComponent } from '../delete/event-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import dayjs from 'dayjs/esm';

@Component({
  standalone: true,
  selector: 'jhi-event',
  templateUrl: './event.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, FormatMediumDatetimePipe],
})
export class EventComponent implements OnInit {
  subscription: Subscription | null = null;
  events?: IEvent[];
  isLoading = false;
  currentUserProfileId?: number | null = null;

  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly eventService = inject(EventService);
  protected readonly accountService = inject(AccountService);
  protected readonly userProfileService = inject(UserProfileService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected dataUtils = inject(DataUtils);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IEvent): number => this.eventService.getEventIdentifier(item);

  ngOnInit(): void {
    this.load();
  }
  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(event: IEvent): void {
    const modalRef = this.modalService.open(EventDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.event = event;
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
    this.events = this.refineData(dataFromBody);
  }

  protected refineData(data: IEvent[]): IEvent[] {
    const now = dayjs();

    return data
      .filter(e => e.endTime != null && !e.endTime.isBefore(now))
      .sort((a, b) => {
        const aStart = a.startTime;
        const bStart = b.startTime;

        if (!aStart && !bStart) {
          return 0;
        }
        if (!aStart) {
          return 1;
        }
        if (!bStart) {
          return -1;
        }

        return aStart.diff(bStart);
      });
  }

  protected fillComponentAttributesFromResponseBody(data: IEvent[] | null): IEvent[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      eagerload: true,
      sort: ['startTime,asc'],
    };
    return this.eventService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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
