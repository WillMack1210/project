import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { FriendStatus } from 'app/entities/enumerations/friend-status.model';
import { FriendshipService } from '../service/friendship.service';
import { IFriendship } from '../friendship.model';
import { FriendshipFormGroup, FriendshipFormService } from './friendship-form.service';

@Component({
  standalone: true,
  selector: 'jhi-friendship-update',
  templateUrl: './friendship-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FriendshipUpdateComponent implements OnInit {
  isSaving = false;
  friendship: IFriendship | null = null;
  friendStatusValues = Object.keys(FriendStatus);

  userProfilesSharedCollection: IUserProfile[] = [];

  protected friendshipService = inject(FriendshipService);
  protected friendshipFormService = inject(FriendshipFormService);
  protected userProfileService = inject(UserProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FriendshipFormGroup = this.friendshipFormService.createFriendshipFormGroup();

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ friendship }) => {
      this.friendship = friendship;
      if (friendship) {
        this.updateForm(friendship);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const friendship = this.friendshipFormService.getFriendship(this.editForm);
    if (friendship.id !== null) {
      this.subscribeToSaveResponse(this.friendshipService.update(friendship));
    } else {
      this.subscribeToSaveResponse(this.friendshipService.create(friendship));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFriendship>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(friendship: IFriendship): void {
    this.friendship = friendship;
    this.friendshipFormService.resetForm(this.editForm, friendship);

    this.userProfilesSharedCollection = this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(
      this.userProfilesSharedCollection,
      friendship.user,
      friendship.friend,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(
            userProfiles,
            this.friendship?.user,
            this.friendship?.friend,
          ),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => (this.userProfilesSharedCollection = userProfiles));
  }
}
