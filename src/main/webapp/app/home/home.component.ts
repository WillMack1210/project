import { Component, OnDestroy, OnInit, inject, signal, NgZone } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

import { FullCalendarModule } from '@fullcalendar/angular';
import dayGridPlugin from '@fullcalendar/daygrid';
import { CalendarOptions } from '@fullcalendar/core';
import { FriendshipExtendedService } from 'app/entities/friendship/service/friendship-extended.service';
import { IFriendship } from 'app/entities/friendship/friendship.model';

@Component({
  standalone: true,
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [SharedModule, RouterModule, FullCalendarModule],
})
export default class HomeComponent implements OnInit, OnDestroy {
  account = signal<Account | null>(null);
  requests = false;
  currentRequests?: IFriendship[];
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin],
    initialView: 'dayGridMonth',
    headerToolbar: {
      left: 'prev next today',
      center: 'title',
      right: 'dayGridMonth dayGridWeek dayGridDay',
    },
    events: [],
    eventClick: this.handleEventClick.bind(this),
  };

  protected friendshipService = inject(FriendshipExtendedService);
  private readonly destroy$ = new Subject<void>();
  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);
  private readonly zone = inject(NgZone);

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: account => {
          this.account.set(account);
          if (account) {
            this.seeRequests();
            this.loadEvents();
          }
        },
      });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  handleEventClick(arg: any): void {
    const eventId = arg.event.id;
    if (eventId) {
      this.router.navigate(['/event', eventId, 'view']);
    }
  }

  seeRequests(): void {
    this.accountService.identity().subscribe({
      next: account => {
        if (!account) {
          return;
        }
        this.http.get<any>(`/api/user-profiles/by-user/${account.login}`).subscribe({
          next: userProfile => {
            const profileId = userProfile.id;
            this.friendshipService.getRequests(profileId).subscribe({
              next: requestsNow => {
                this.currentRequests = requestsNow;
                if (this.currentRequests.length > 0) {
                  this.requests = true;
                }
              },
            });
          },
        });
      },
    });
  }

  loadEvents(): void {
    this.accountService.identity().subscribe({
      next: account => {
        if (!account) {
          return;
        }
        this.http.get<any>(`/api/user-profiles/by-user/${account.login}`).subscribe({
          next: userProfile => {
            const profileId = userProfile.id;
            this.http.get<any[]>('/api/events').subscribe({
              next: events => {
                const filteredEvents = events
                  .filter(e => Number(e.owner?.id) === Number(profileId))
                  .map(e => ({
                    id: e.id.toString(),
                    title: e.title,
                    start: e.startTime,
                    end: e.endTime,
                  }));
                this.zone.run(() => {
                  this.calendarOptions = {
                    ...this.calendarOptions,
                    events: filteredEvents,
                  };
                });
              },
              error: error => console.error('Error loading events:', error),
            });
          },
          error: error => console.error('Error loading user profile:', error),
        });
      },
    });
  }
}
