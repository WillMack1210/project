import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISuggestion, NewSuggestion } from '../suggestion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISuggestion for edit and NewSuggestionFormGroupInput for create.
 */
type SuggestionFormGroupInput = ISuggestion | PartialWithRequiredKeyOf<NewSuggestion>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISuggestion | NewSuggestion> = Omit<T, 'suggestedStart' | 'suggestedEnd'> & {
  suggestedStart?: string | null;
  suggestedEnd?: string | null;
};

type SuggestionFormRawValue = FormValueOf<ISuggestion>;

type NewSuggestionFormRawValue = FormValueOf<NewSuggestion>;

type SuggestionFormDefaults = Pick<NewSuggestion, 'id' | 'suggestedStart' | 'suggestedEnd' | 'findTimes'>;

type SuggestionFormGroupContent = {
  id: FormControl<SuggestionFormRawValue['id'] | NewSuggestion['id']>;
  suggestedStart: FormControl<SuggestionFormRawValue['suggestedStart']>;
  suggestedEnd: FormControl<SuggestionFormRawValue['suggestedEnd']>;
  findTimes: FormControl<SuggestionFormRawValue['findTimes']>;
};

export type SuggestionFormGroup = FormGroup<SuggestionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SuggestionFormService {
  createSuggestionFormGroup(suggestion: SuggestionFormGroupInput = { id: null }): SuggestionFormGroup {
    const suggestionRawValue = this.convertSuggestionToSuggestionRawValue({
      ...this.getFormDefaults(),
      ...suggestion,
    });
    return new FormGroup<SuggestionFormGroupContent>({
      id: new FormControl(
        { value: suggestionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      suggestedStart: new FormControl(suggestionRawValue.suggestedStart, {
        validators: [Validators.required],
      }),
      suggestedEnd: new FormControl(suggestionRawValue.suggestedEnd, {
        validators: [Validators.required],
      }),
      findTimes: new FormControl(suggestionRawValue.findTimes ?? []),
    });
  }

  getSuggestion(form: SuggestionFormGroup): ISuggestion | NewSuggestion {
    return this.convertSuggestionRawValueToSuggestion(form.getRawValue() as SuggestionFormRawValue | NewSuggestionFormRawValue);
  }

  resetForm(form: SuggestionFormGroup, suggestion: SuggestionFormGroupInput): void {
    const suggestionRawValue = this.convertSuggestionToSuggestionRawValue({ ...this.getFormDefaults(), ...suggestion });
    form.reset(
      {
        ...suggestionRawValue,
        id: { value: suggestionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SuggestionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      suggestedStart: currentTime,
      suggestedEnd: currentTime,
      findTimes: [],
    };
  }

  private convertSuggestionRawValueToSuggestion(
    rawSuggestion: SuggestionFormRawValue | NewSuggestionFormRawValue,
  ): ISuggestion | NewSuggestion {
    return {
      ...rawSuggestion,
      suggestedStart: dayjs(rawSuggestion.suggestedStart, DATE_TIME_FORMAT),
      suggestedEnd: dayjs(rawSuggestion.suggestedEnd, DATE_TIME_FORMAT),
    };
  }

  private convertSuggestionToSuggestionRawValue(
    suggestion: ISuggestion | (Partial<NewSuggestion> & SuggestionFormDefaults),
  ): SuggestionFormRawValue | PartialWithRequiredKeyOf<NewSuggestionFormRawValue> {
    return {
      ...suggestion,
      suggestedStart: suggestion.suggestedStart ? suggestion.suggestedStart.format(DATE_TIME_FORMAT) : undefined,
      suggestedEnd: suggestion.suggestedEnd ? suggestion.suggestedEnd.format(DATE_TIME_FORMAT) : undefined,
      findTimes: suggestion.findTimes ?? [],
    };
  }
}
