import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IFriendship } from '../friendship.model';
import { FriendshipService } from '../service/friendship.service';

@Component({
  standalone: true,
  templateUrl: './friendship-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class FriendshipDeleteDialogComponent {
  friendship?: IFriendship;

  protected friendshipService = inject(FriendshipService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.friendshipService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
