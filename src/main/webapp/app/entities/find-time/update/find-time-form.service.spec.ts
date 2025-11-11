import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../find-time.test-samples';

import { FindTimeFormService } from './find-time-form.service';

describe('FindTime Form Service', () => {
  let service: FindTimeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FindTimeFormService);
  });

  describe('Service methods', () => {
    describe('createFindTimeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFindTimeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            requestStart: expect.any(Object),
            requestEnd: expect.any(Object),
            length: expect.any(Object),
            requester: expect.any(Object),
            participants: expect.any(Object),
            suggestions: expect.any(Object),
          }),
        );
      });

      it('passing IFindTime should create a new form with FormGroup', () => {
        const formGroup = service.createFindTimeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            requestStart: expect.any(Object),
            requestEnd: expect.any(Object),
            length: expect.any(Object),
            requester: expect.any(Object),
            participants: expect.any(Object),
            suggestions: expect.any(Object),
          }),
        );
      });
    });

    describe('getFindTime', () => {
      it('should return NewFindTime for default FindTime initial value', () => {
        const formGroup = service.createFindTimeFormGroup(sampleWithNewData);

        const findTime = service.getFindTime(formGroup) as any;

        expect(findTime).toMatchObject(sampleWithNewData);
      });

      it('should return NewFindTime for empty FindTime initial value', () => {
        const formGroup = service.createFindTimeFormGroup();

        const findTime = service.getFindTime(formGroup) as any;

        expect(findTime).toMatchObject({});
      });

      it('should return IFindTime', () => {
        const formGroup = service.createFindTimeFormGroup(sampleWithRequiredData);

        const findTime = service.getFindTime(formGroup) as any;

        expect(findTime).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFindTime should not enable id FormControl', () => {
        const formGroup = service.createFindTimeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFindTime should disable id FormControl', () => {
        const formGroup = service.createFindTimeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
