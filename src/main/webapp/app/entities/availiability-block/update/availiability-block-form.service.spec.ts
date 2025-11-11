import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../availiability-block.test-samples';

import { AvailiabilityBlockFormService } from './availiability-block-form.service';

describe('AvailiabilityBlock Form Service', () => {
  let service: AvailiabilityBlockFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AvailiabilityBlockFormService);
  });

  describe('Service methods', () => {
    describe('createAvailiabilityBlockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAvailiabilityBlockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDateTime: expect.any(Object),
            endDateTime: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IAvailiabilityBlock should create a new form with FormGroup', () => {
        const formGroup = service.createAvailiabilityBlockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDateTime: expect.any(Object),
            endDateTime: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getAvailiabilityBlock', () => {
      it('should return NewAvailiabilityBlock for default AvailiabilityBlock initial value', () => {
        const formGroup = service.createAvailiabilityBlockFormGroup(sampleWithNewData);

        const availiabilityBlock = service.getAvailiabilityBlock(formGroup) as any;

        expect(availiabilityBlock).toMatchObject(sampleWithNewData);
      });

      it('should return NewAvailiabilityBlock for empty AvailiabilityBlock initial value', () => {
        const formGroup = service.createAvailiabilityBlockFormGroup();

        const availiabilityBlock = service.getAvailiabilityBlock(formGroup) as any;

        expect(availiabilityBlock).toMatchObject({});
      });

      it('should return IAvailiabilityBlock', () => {
        const formGroup = service.createAvailiabilityBlockFormGroup(sampleWithRequiredData);

        const availiabilityBlock = service.getAvailiabilityBlock(formGroup) as any;

        expect(availiabilityBlock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAvailiabilityBlock should not enable id FormControl', () => {
        const formGroup = service.createAvailiabilityBlockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAvailiabilityBlock should disable id FormControl', () => {
        const formGroup = service.createAvailiabilityBlockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
