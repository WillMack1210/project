import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IScheduleRequest, NewScheduleRequest } from '../schedule-request.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IScheduleRequest for edit and NewScheduleRequestFormGroupInput for create.
 */
type ScheduleRequestFormGroupInput = IScheduleRequest | PartialWithRequiredKeyOf<NewScheduleRequest>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IScheduleRequest | NewScheduleRequest> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type ScheduleRequestFormRawValue = FormValueOf<IScheduleRequest>;

type NewScheduleRequestFormRawValue = FormValueOf<NewScheduleRequest>;

type ScheduleRequestFormDefaults = Pick<NewScheduleRequest, 'id' | 'startDate' | 'endDate'>;

type ScheduleRequestFormGroupContent = {
  id: FormControl<ScheduleRequestFormRawValue['id'] | NewScheduleRequest['id']>;
  startDate: FormControl<ScheduleRequestFormRawValue['startDate']>;
  endDate: FormControl<ScheduleRequestFormRawValue['endDate']>;
  scheduleDescription: FormControl<ScheduleRequestFormRawValue['scheduleDescription']>;
  intensity: FormControl<ScheduleRequestFormRawValue['intensity']>;
  user: FormControl<ScheduleRequestFormRawValue['user']>;
};

export type ScheduleRequestFormGroup = FormGroup<ScheduleRequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ScheduleRequestFormService {
  createScheduleRequestFormGroup(scheduleRequest: ScheduleRequestFormGroupInput = { id: null }): ScheduleRequestFormGroup {
    const scheduleRequestRawValue = this.convertScheduleRequestToScheduleRequestRawValue({
      ...this.getFormDefaults(),
      ...scheduleRequest,
    });
    return new FormGroup<ScheduleRequestFormGroupContent>({
      id: new FormControl(
        { value: scheduleRequestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      startDate: new FormControl(scheduleRequestRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(scheduleRequestRawValue.endDate, {
        validators: [Validators.required],
      }),
      scheduleDescription: new FormControl(scheduleRequestRawValue.scheduleDescription, {
        validators: [Validators.required],
      }),
      intensity: new FormControl(scheduleRequestRawValue.intensity, {
        validators: [Validators.required],
      }),
      user: new FormControl(scheduleRequestRawValue.user),
    });
  }

  getScheduleRequest(form: ScheduleRequestFormGroup): IScheduleRequest | NewScheduleRequest {
    return this.convertScheduleRequestRawValueToScheduleRequest(
      form.getRawValue() as ScheduleRequestFormRawValue | NewScheduleRequestFormRawValue,
    );
  }

  resetForm(form: ScheduleRequestFormGroup, scheduleRequest: ScheduleRequestFormGroupInput): void {
    const scheduleRequestRawValue = this.convertScheduleRequestToScheduleRequestRawValue({ ...this.getFormDefaults(), ...scheduleRequest });
    form.reset(
      {
        ...scheduleRequestRawValue,
        id: { value: scheduleRequestRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ScheduleRequestFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
    };
  }

  private convertScheduleRequestRawValueToScheduleRequest(
    rawScheduleRequest: ScheduleRequestFormRawValue | NewScheduleRequestFormRawValue,
  ): IScheduleRequest | NewScheduleRequest {
    return {
      ...rawScheduleRequest,
      startDate: dayjs(rawScheduleRequest.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawScheduleRequest.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertScheduleRequestToScheduleRequestRawValue(
    scheduleRequest: IScheduleRequest | (Partial<NewScheduleRequest> & ScheduleRequestFormDefaults),
  ): ScheduleRequestFormRawValue | PartialWithRequiredKeyOf<NewScheduleRequestFormRawValue> {
    return {
      ...scheduleRequest,
      startDate: scheduleRequest.startDate ? scheduleRequest.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: scheduleRequest.endDate ? scheduleRequest.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
