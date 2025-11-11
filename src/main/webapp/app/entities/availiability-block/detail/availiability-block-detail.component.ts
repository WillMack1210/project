import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IAvailiabilityBlock } from '../availiability-block.model';

@Component({
  standalone: true,
  selector: 'jhi-availiability-block-detail',
  templateUrl: './availiability-block-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class AvailiabilityBlockDetailComponent {
  availiabilityBlock = input<IAvailiabilityBlock | null>(null);

  previousState(): void {
    window.history.back();
  }
}
