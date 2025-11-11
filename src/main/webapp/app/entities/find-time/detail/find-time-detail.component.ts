import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IFindTime } from '../find-time.model';

@Component({
  standalone: true,
  selector: 'jhi-find-time-detail',
  templateUrl: './find-time-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class FindTimeDetailComponent {
  findTime = input<IFindTime | null>(null);

  previousState(): void {
    window.history.back();
  }
}
