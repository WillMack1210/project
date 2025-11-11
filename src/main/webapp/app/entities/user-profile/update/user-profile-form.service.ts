import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUserProfile, NewUserProfile } from '../user-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserProfile for edit and NewUserProfileFormGroupInput for create.
 */
type UserProfileFormGroupInput = IUserProfile | PartialWithRequiredKeyOf<NewUserProfile>;

type UserProfileFormDefaults = Pick<NewUserProfile, 'id' | 'sharedEvents' | 'findTimes'>;

type UserProfileFormGroupContent = {
  id: FormControl<IUserProfile['id'] | NewUserProfile['id']>;
  username: FormControl<IUserProfile['username']>;
  fullName: FormControl<IUserProfile['fullName']>;
  profilePicture: FormControl<IUserProfile['profilePicture']>;
  profilePictureContentType: FormControl<IUserProfile['profilePictureContentType']>;
  settings: FormControl<IUserProfile['settings']>;
  user: FormControl<IUserProfile['user']>;
  sharedEvents: FormControl<IUserProfile['sharedEvents']>;
  findTimes: FormControl<IUserProfile['findTimes']>;
};

export type UserProfileFormGroup = FormGroup<UserProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserProfileFormService {
  createUserProfileFormGroup(userProfile: UserProfileFormGroupInput = { id: null }): UserProfileFormGroup {
    const userProfileRawValue = {
      ...this.getFormDefaults(),
      ...userProfile,
    };
    return new FormGroup<UserProfileFormGroupContent>({
      id: new FormControl(
        { value: userProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      username: new FormControl(userProfileRawValue.username, {
        validators: [Validators.required],
      }),
      fullName: new FormControl(userProfileRawValue.fullName, {
        validators: [Validators.required],
      }),
      profilePicture: new FormControl(userProfileRawValue.profilePicture),
      profilePictureContentType: new FormControl(userProfileRawValue.profilePictureContentType),
      settings: new FormControl(userProfileRawValue.settings),
      user: new FormControl(userProfileRawValue.user),
      sharedEvents: new FormControl(userProfileRawValue.sharedEvents ?? []),
      findTimes: new FormControl(userProfileRawValue.findTimes ?? []),
    });
  }

  getUserProfile(form: UserProfileFormGroup): IUserProfile | NewUserProfile {
    return form.getRawValue() as IUserProfile | NewUserProfile;
  }

  resetForm(form: UserProfileFormGroup, userProfile: UserProfileFormGroupInput): void {
    const userProfileRawValue = { ...this.getFormDefaults(), ...userProfile };
    form.reset(
      {
        ...userProfileRawValue,
        id: { value: userProfileRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UserProfileFormDefaults {
    return {
      id: null,
      sharedEvents: [],
      findTimes: [],
    };
  }
}
