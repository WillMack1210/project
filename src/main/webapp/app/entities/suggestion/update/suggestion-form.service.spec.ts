import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../suggestion.test-samples';

import { SuggestionFormService } from './suggestion-form.service';

describe('Suggestion Form Service', () => {
  let service: SuggestionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SuggestionFormService);
  });

  describe('Service methods', () => {
    describe('createSuggestionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSuggestionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            suggestedStart: expect.any(Object),
            suggestedEnd: expect.any(Object),
            findTimes: expect.any(Object),
          }),
        );
      });

      it('passing ISuggestion should create a new form with FormGroup', () => {
        const formGroup = service.createSuggestionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            suggestedStart: expect.any(Object),
            suggestedEnd: expect.any(Object),
            findTimes: expect.any(Object),
          }),
        );
      });
    });

    describe('getSuggestion', () => {
      it('should return NewSuggestion for default Suggestion initial value', () => {
        const formGroup = service.createSuggestionFormGroup(sampleWithNewData);

        const suggestion = service.getSuggestion(formGroup) as any;

        expect(suggestion).toMatchObject(sampleWithNewData);
      });

      it('should return NewSuggestion for empty Suggestion initial value', () => {
        const formGroup = service.createSuggestionFormGroup();

        const suggestion = service.getSuggestion(formGroup) as any;

        expect(suggestion).toMatchObject({});
      });

      it('should return ISuggestion', () => {
        const formGroup = service.createSuggestionFormGroup(sampleWithRequiredData);

        const suggestion = service.getSuggestion(formGroup) as any;

        expect(suggestion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISuggestion should not enable id FormControl', () => {
        const formGroup = service.createSuggestionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSuggestion should disable id FormControl', () => {
        const formGroup = service.createSuggestionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
