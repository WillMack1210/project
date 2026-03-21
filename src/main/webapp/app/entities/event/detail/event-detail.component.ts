import { Component, inject, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { DataUtils } from 'app/core/util/data-util.service';
import { IEvent } from '../event.model';
import { EventService } from '../service/event.service';

@Component({
  standalone: true,
  selector: 'jhi-event-detail',
  templateUrl: './event-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class EventDetailComponent {
  event = input<IEvent | null>(null);

  protected dataUtils = inject(DataUtils);
  protected eventService = inject(EventService);

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }

  deleteEvent(): void {
    const event = this.event();

    if (event?.id == null) {
      return;
    }

    const confirmed = window.confirm('Are you sure you want to delete this event?');
    if (!confirmed) {
      return;
    }

    this.eventService.delete(event.id).subscribe(() => this.previousState());
  }
}
