import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IAvailiabilityBlock } from '../availiability-block.model';
import { AvailiabilityBlockService } from '../service/availiability-block.service';
import { AvailiabilityBlockFormGroup, AvailiabilityBlockFormService } from './availiability-block-form.service';

@Component({
  standalone: true,
  selector: 'jhi-availiability-block-update',
  templateUrl: './availiability-block-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AvailiabilityBlockUpdateComponent implements OnInit {
  isSaving = false;
  availiabilityBlock: IAvailiabilityBlock | null = null;

  userProfilesSharedCollection: IUserProfile[] = [];

  protected availiabilityBlockService = inject(AvailiabilityBlockService);
  protected availiabilityBlockFormService = inject(AvailiabilityBlockFormService);
  protected userProfileService = inject(UserProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AvailiabilityBlockFormGroup = this.availiabilityBlockFormService.createAvailiabilityBlockFormGroup();

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ availiabilityBlock }) => {
      this.availiabilityBlock = availiabilityBlock;
      if (availiabilityBlock) {
        this.updateForm(availiabilityBlock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const availiabilityBlock = this.availiabilityBlockFormService.getAvailiabilityBlock(this.editForm);
    if (availiabilityBlock.id !== null) {
      this.subscribeToSaveResponse(this.availiabilityBlockService.update(availiabilityBlock));
    } else {
      this.subscribeToSaveResponse(this.availiabilityBlockService.create(availiabilityBlock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAvailiabilityBlock>>): void {
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

  protected updateForm(availiabilityBlock: IAvailiabilityBlock): void {
    this.availiabilityBlock = availiabilityBlock;
    this.availiabilityBlockFormService.resetForm(this.editForm, availiabilityBlock);

    this.userProfilesSharedCollection = this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(
      this.userProfilesSharedCollection,
      availiabilityBlock.user,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, this.availiabilityBlock?.user),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => (this.userProfilesSharedCollection = userProfiles));
  }
}
