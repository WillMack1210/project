import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { ISuggestion } from 'app/entities/suggestion/suggestion.model';
import { SuggestionService } from 'app/entities/suggestion/service/suggestion.service';
import { FindTimeService } from '../service/find-time.service';
import { IFindTime } from '../find-time.model';
import { FindTimeFormGroup, FindTimeFormService } from './find-time-form.service';

@Component({
  standalone: true,
  selector: 'jhi-find-time-update',
  templateUrl: './find-time-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FindTimeUpdateComponent implements OnInit {
  isSaving = false;
  findTime: IFindTime | null = null;

  userProfilesSharedCollection: IUserProfile[] = [];
  suggestionsSharedCollection: ISuggestion[] = [];

  protected findTimeService = inject(FindTimeService);
  protected findTimeFormService = inject(FindTimeFormService);
  protected userProfileService = inject(UserProfileService);
  protected suggestionService = inject(SuggestionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FindTimeFormGroup = this.findTimeFormService.createFindTimeFormGroup();

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  compareSuggestion = (o1: ISuggestion | null, o2: ISuggestion | null): boolean => this.suggestionService.compareSuggestion(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ findTime }) => {
      this.findTime = findTime;
      if (findTime) {
        this.updateForm(findTime);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const findTime = this.findTimeFormService.getFindTime(this.editForm);
    if (findTime.id !== null) {
      this.subscribeToSaveResponse(this.findTimeService.update(findTime));
    } else {
      this.subscribeToSaveResponse(this.findTimeService.create(findTime));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFindTime>>): void {
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

  protected updateForm(findTime: IFindTime): void {
    this.findTime = findTime;
    this.findTimeFormService.resetForm(this.editForm, findTime);

    this.userProfilesSharedCollection = this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(
      this.userProfilesSharedCollection,
      findTime.requester,
      ...(findTime.participants ?? []),
    );
    this.suggestionsSharedCollection = this.suggestionService.addSuggestionToCollectionIfMissing<ISuggestion>(
      this.suggestionsSharedCollection,
      ...(findTime.suggestions ?? []),
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
            this.findTime?.requester,
            ...(this.findTime?.participants ?? []),
          ),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => (this.userProfilesSharedCollection = userProfiles));

    this.suggestionService
      .query()
      .pipe(map((res: HttpResponse<ISuggestion[]>) => res.body ?? []))
      .pipe(
        map((suggestions: ISuggestion[]) =>
          this.suggestionService.addSuggestionToCollectionIfMissing<ISuggestion>(suggestions, ...(this.findTime?.suggestions ?? [])),
        ),
      )
      .subscribe((suggestions: ISuggestion[]) => (this.suggestionsSharedCollection = suggestions));
  }
}
