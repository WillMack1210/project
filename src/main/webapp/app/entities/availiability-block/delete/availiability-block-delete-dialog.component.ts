import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IAvailiabilityBlock } from '../availiability-block.model';
import { AvailiabilityBlockService } from '../service/availiability-block.service';

@Component({
  standalone: true,
  templateUrl: './availiability-block-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class AvailiabilityBlockDeleteDialogComponent {
  availiabilityBlock?: IAvailiabilityBlock;

  protected availiabilityBlockService = inject(AvailiabilityBlockService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.availiabilityBlockService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
