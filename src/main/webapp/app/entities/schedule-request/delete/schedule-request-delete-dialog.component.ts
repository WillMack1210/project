import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IScheduleRequest } from '../schedule-request.model';
import { ScheduleRequestService } from '../service/schedule-request.service';

@Component({
  standalone: true,
  templateUrl: './schedule-request-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ScheduleRequestDeleteDialogComponent {
  scheduleRequest?: IScheduleRequest;

  protected scheduleRequestService = inject(ScheduleRequestService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.scheduleRequestService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
