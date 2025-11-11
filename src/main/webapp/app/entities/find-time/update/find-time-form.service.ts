import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFindTime, NewFindTime } from '../find-time.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFindTime for edit and NewFindTimeFormGroupInput for create.
 */
type FindTimeFormGroupInput = IFindTime | PartialWithRequiredKeyOf<NewFindTime>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFindTime | NewFindTime> = Omit<T, 'requestStart' | 'requestEnd'> & {
  requestStart?: string | null;
  requestEnd?: string | null;
};

type FindTimeFormRawValue = FormValueOf<IFindTime>;

type NewFindTimeFormRawValue = FormValueOf<NewFindTime>;

type FindTimeFormDefaults = Pick<NewFindTime, 'id' | 'requestStart' | 'requestEnd' | 'participants' | 'suggestions'>;

type FindTimeFormGroupContent = {
  id: FormControl<FindTimeFormRawValue['id'] | NewFindTime['id']>;
  requestStart: FormControl<FindTimeFormRawValue['requestStart']>;
  requestEnd: FormControl<FindTimeFormRawValue['requestEnd']>;
  length: FormControl<FindTimeFormRawValue['length']>;
  requester: FormControl<FindTimeFormRawValue['requester']>;
  participants: FormControl<FindTimeFormRawValue['participants']>;
  suggestions: FormControl<FindTimeFormRawValue['suggestions']>;
};

export type FindTimeFormGroup = FormGroup<FindTimeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FindTimeFormService {
  createFindTimeFormGroup(findTime: FindTimeFormGroupInput = { id: null }): FindTimeFormGroup {
    const findTimeRawValue = this.convertFindTimeToFindTimeRawValue({
      ...this.getFormDefaults(),
      ...findTime,
    });
    return new FormGroup<FindTimeFormGroupContent>({
      id: new FormControl(
        { value: findTimeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      requestStart: new FormControl(findTimeRawValue.requestStart, {
        validators: [Validators.required],
      }),
      requestEnd: new FormControl(findTimeRawValue.requestEnd, {
        validators: [Validators.required],
      }),
      length: new FormControl(findTimeRawValue.length, {
        validators: [Validators.required],
      }),
      requester: new FormControl(findTimeRawValue.requester),
      participants: new FormControl(findTimeRawValue.participants ?? []),
      suggestions: new FormControl(findTimeRawValue.suggestions ?? []),
    });
  }

  getFindTime(form: FindTimeFormGroup): IFindTime | NewFindTime {
    return this.convertFindTimeRawValueToFindTime(form.getRawValue() as FindTimeFormRawValue | NewFindTimeFormRawValue);
  }

  resetForm(form: FindTimeFormGroup, findTime: FindTimeFormGroupInput): void {
    const findTimeRawValue = this.convertFindTimeToFindTimeRawValue({ ...this.getFormDefaults(), ...findTime });
    form.reset(
      {
        ...findTimeRawValue,
        id: { value: findTimeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): FindTimeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      requestStart: currentTime,
      requestEnd: currentTime,
      participants: [],
      suggestions: [],
    };
  }

  private convertFindTimeRawValueToFindTime(rawFindTime: FindTimeFormRawValue | NewFindTimeFormRawValue): IFindTime | NewFindTime {
    return {
      ...rawFindTime,
      requestStart: dayjs(rawFindTime.requestStart, DATE_TIME_FORMAT),
      requestEnd: dayjs(rawFindTime.requestEnd, DATE_TIME_FORMAT),
    };
  }

  private convertFindTimeToFindTimeRawValue(
    findTime: IFindTime | (Partial<NewFindTime> & FindTimeFormDefaults),
  ): FindTimeFormRawValue | PartialWithRequiredKeyOf<NewFindTimeFormRawValue> {
    return {
      ...findTime,
      requestStart: findTime.requestStart ? findTime.requestStart.format(DATE_TIME_FORMAT) : undefined,
      requestEnd: findTime.requestEnd ? findTime.requestEnd.format(DATE_TIME_FORMAT) : undefined,
      participants: findTime.participants ?? [],
      suggestions: findTime.suggestions ?? [],
    };
  }
}
