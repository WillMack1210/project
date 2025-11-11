import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IFindTime } from '../find-time.model';
import { FindTimeService } from '../service/find-time.service';

@Component({
  standalone: true,
  templateUrl: './find-time-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class FindTimeDeleteDialogComponent {
  findTime?: IFindTime;

  protected findTimeService = inject(FindTimeService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.findTimeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
