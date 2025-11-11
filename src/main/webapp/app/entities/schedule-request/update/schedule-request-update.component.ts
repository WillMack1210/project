import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { ScheduleIntensity } from 'app/entities/enumerations/schedule-intensity.model';
import { ScheduleRequestService } from '../service/schedule-request.service';
import { IScheduleRequest } from '../schedule-request.model';
import { ScheduleRequestFormGroup, ScheduleRequestFormService } from './schedule-request-form.service';

@Component({
  standalone: true,
  selector: 'jhi-schedule-request-update',
  templateUrl: './schedule-request-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ScheduleRequestUpdateComponent implements OnInit {
  isSaving = false;
  scheduleRequest: IScheduleRequest | null = null;
  scheduleIntensityValues = Object.keys(ScheduleIntensity);

  userProfilesSharedCollection: IUserProfile[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected scheduleRequestService = inject(ScheduleRequestService);
  protected scheduleRequestFormService = inject(ScheduleRequestFormService);
  protected userProfileService = inject(UserProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ScheduleRequestFormGroup = this.scheduleRequestFormService.createScheduleRequestFormGroup();

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ scheduleRequest }) => {
      this.scheduleRequest = scheduleRequest;
      if (scheduleRequest) {
        this.updateForm(scheduleRequest);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('teamproject24App.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const scheduleRequest = this.scheduleRequestFormService.getScheduleRequest(this.editForm);
    if (scheduleRequest.id !== null) {
      this.subscribeToSaveResponse(this.scheduleRequestService.update(scheduleRequest));
    } else {
      this.subscribeToSaveResponse(this.scheduleRequestService.create(scheduleRequest));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IScheduleRequest>>): void {
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

  protected updateForm(scheduleRequest: IScheduleRequest): void {
    this.scheduleRequest = scheduleRequest;
    this.scheduleRequestFormService.resetForm(this.editForm, scheduleRequest);

    this.userProfilesSharedCollection = this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(
      this.userProfilesSharedCollection,
      scheduleRequest.user,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, this.scheduleRequest?.user),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => (this.userProfilesSharedCollection = userProfiles));
  }
}
