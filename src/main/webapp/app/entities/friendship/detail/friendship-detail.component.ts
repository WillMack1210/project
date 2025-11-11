import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IFriendship } from '../friendship.model';

@Component({
  standalone: true,
  selector: 'jhi-friendship-detail',
  templateUrl: './friendship-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class FriendshipDetailComponent {
  friendship = input<IFriendship | null>(null);

  previousState(): void {
    window.history.back();
  }
}
