import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ISuggestion } from '../suggestion.model';

@Component({
  standalone: true,
  selector: 'jhi-suggestion-detail',
  templateUrl: './suggestion-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class SuggestionDetailComponent {
  suggestion = input<ISuggestion | null>(null);

  previousState(): void {
    window.history.back();
  }
}
