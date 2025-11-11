import { Component, ElementRef, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IFindTime } from 'app/entities/find-time/find-time.model';
import { FindTimeService } from 'app/entities/find-time/service/find-time.service';
import { UserProfileService } from '../service/user-profile.service';
import { IUserProfile } from '../user-profile.model';
import { UserProfileFormGroup, UserProfileFormService } from './user-profile-form.service';

@Component({
  standalone: true,
  selector: 'jhi-user-profile-update',
  templateUrl: './user-profile-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class UserProfileUpdateComponent implements OnInit {
  isSaving = false;
  userProfile: IUserProfile | null = null;

  usersSharedCollection: IUser[] = [];
  eventsSharedCollection: IEvent[] = [];
  findTimesSharedCollection: IFindTime[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected userProfileService = inject(UserProfileService);
  protected userProfileFormService = inject(UserProfileFormService);
  protected userService = inject(UserService);
  protected eventService = inject(EventService);
  protected findTimeService = inject(FindTimeService);
  protected elementRef = inject(ElementRef);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserProfileFormGroup = this.userProfileFormService.createUserProfileFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  compareFindTime = (o1: IFindTime | null, o2: IFindTime | null): boolean => this.findTimeService.compareFindTime(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userProfile }) => {
      this.userProfile = userProfile;
      if (userProfile) {
        this.updateForm(userProfile);
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

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector(`#${idInput}`)) {
      this.elementRef.nativeElement.querySelector(`#${idInput}`).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userProfile = this.userProfileFormService.getUserProfile(this.editForm);
    if (userProfile.id !== null) {
      this.subscribeToSaveResponse(this.userProfileService.update(userProfile));
    } else {
      this.subscribeToSaveResponse(this.userProfileService.create(userProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserProfile>>): void {
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

  protected updateForm(userProfile: IUserProfile): void {
    this.userProfile = userProfile;
    this.userProfileFormService.resetForm(this.editForm, userProfile);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, userProfile.user);
    this.eventsSharedCollection = this.eventService.addEventToCollectionIfMissing<IEvent>(
      this.eventsSharedCollection,
      ...(userProfile.sharedEvents ?? []),
    );
    this.findTimesSharedCollection = this.findTimeService.addFindTimeToCollectionIfMissing<IFindTime>(
      this.findTimesSharedCollection,
      ...(userProfile.findTimes ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userProfile?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(
        map((events: IEvent[]) =>
          this.eventService.addEventToCollectionIfMissing<IEvent>(events, ...(this.userProfile?.sharedEvents ?? [])),
        ),
      )
      .subscribe((events: IEvent[]) => (this.eventsSharedCollection = events));

    this.findTimeService
      .query()
      .pipe(map((res: HttpResponse<IFindTime[]>) => res.body ?? []))
      .pipe(
        map((findTimes: IFindTime[]) =>
          this.findTimeService.addFindTimeToCollectionIfMissing<IFindTime>(findTimes, ...(this.userProfile?.findTimes ?? [])),
        ),
      )
      .subscribe((findTimes: IFindTime[]) => (this.findTimesSharedCollection = findTimes));
  }
}
