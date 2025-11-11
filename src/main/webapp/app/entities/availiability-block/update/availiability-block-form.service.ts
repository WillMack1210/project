import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAvailiabilityBlock, NewAvailiabilityBlock } from '../availiability-block.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAvailiabilityBlock for edit and NewAvailiabilityBlockFormGroupInput for create.
 */
type AvailiabilityBlockFormGroupInput = IAvailiabilityBlock | PartialWithRequiredKeyOf<NewAvailiabilityBlock>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAvailiabilityBlock | NewAvailiabilityBlock> = Omit<T, 'startDateTime' | 'endDateTime'> & {
  startDateTime?: string | null;
  endDateTime?: string | null;
};

type AvailiabilityBlockFormRawValue = FormValueOf<IAvailiabilityBlock>;

type NewAvailiabilityBlockFormRawValue = FormValueOf<NewAvailiabilityBlock>;

type AvailiabilityBlockFormDefaults = Pick<NewAvailiabilityBlock, 'id' | 'startDateTime' | 'endDateTime'>;

type AvailiabilityBlockFormGroupContent = {
  id: FormControl<AvailiabilityBlockFormRawValue['id'] | NewAvailiabilityBlock['id']>;
  startDateTime: FormControl<AvailiabilityBlockFormRawValue['startDateTime']>;
  endDateTime: FormControl<AvailiabilityBlockFormRawValue['endDateTime']>;
  user: FormControl<AvailiabilityBlockFormRawValue['user']>;
};

export type AvailiabilityBlockFormGroup = FormGroup<AvailiabilityBlockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AvailiabilityBlockFormService {
  createAvailiabilityBlockFormGroup(availiabilityBlock: AvailiabilityBlockFormGroupInput = { id: null }): AvailiabilityBlockFormGroup {
    const availiabilityBlockRawValue = this.convertAvailiabilityBlockToAvailiabilityBlockRawValue({
      ...this.getFormDefaults(),
      ...availiabilityBlock,
    });
    return new FormGroup<AvailiabilityBlockFormGroupContent>({
      id: new FormControl(
        { value: availiabilityBlockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      startDateTime: new FormControl(availiabilityBlockRawValue.startDateTime, {
        validators: [Validators.required],
      }),
      endDateTime: new FormControl(availiabilityBlockRawValue.endDateTime, {
        validators: [Validators.required],
      }),
      user: new FormControl(availiabilityBlockRawValue.user),
    });
  }

  getAvailiabilityBlock(form: AvailiabilityBlockFormGroup): IAvailiabilityBlock | NewAvailiabilityBlock {
    return this.convertAvailiabilityBlockRawValueToAvailiabilityBlock(
      form.getRawValue() as AvailiabilityBlockFormRawValue | NewAvailiabilityBlockFormRawValue,
    );
  }

  resetForm(form: AvailiabilityBlockFormGroup, availiabilityBlock: AvailiabilityBlockFormGroupInput): void {
    const availiabilityBlockRawValue = this.convertAvailiabilityBlockToAvailiabilityBlockRawValue({
      ...this.getFormDefaults(),
      ...availiabilityBlock,
    });
    form.reset(
      {
        ...availiabilityBlockRawValue,
        id: { value: availiabilityBlockRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AvailiabilityBlockFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDateTime: currentTime,
      endDateTime: currentTime,
    };
  }

  private convertAvailiabilityBlockRawValueToAvailiabilityBlock(
    rawAvailiabilityBlock: AvailiabilityBlockFormRawValue | NewAvailiabilityBlockFormRawValue,
  ): IAvailiabilityBlock | NewAvailiabilityBlock {
    return {
      ...rawAvailiabilityBlock,
      startDateTime: dayjs(rawAvailiabilityBlock.startDateTime, DATE_TIME_FORMAT),
      endDateTime: dayjs(rawAvailiabilityBlock.endDateTime, DATE_TIME_FORMAT),
    };
  }

  private convertAvailiabilityBlockToAvailiabilityBlockRawValue(
    availiabilityBlock: IAvailiabilityBlock | (Partial<NewAvailiabilityBlock> & AvailiabilityBlockFormDefaults),
  ): AvailiabilityBlockFormRawValue | PartialWithRequiredKeyOf<NewAvailiabilityBlockFormRawValue> {
    return {
      ...availiabilityBlock,
      startDateTime: availiabilityBlock.startDateTime ? availiabilityBlock.startDateTime.format(DATE_TIME_FORMAT) : undefined,
      endDateTime: availiabilityBlock.endDateTime ? availiabilityBlock.endDateTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
