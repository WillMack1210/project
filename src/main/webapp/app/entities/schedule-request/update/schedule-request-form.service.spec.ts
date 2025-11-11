import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../schedule-request.test-samples';

import { ScheduleRequestFormService } from './schedule-request-form.service';

describe('ScheduleRequest Form Service', () => {
  let service: ScheduleRequestFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScheduleRequestFormService);
  });

  describe('Service methods', () => {
    describe('createScheduleRequestFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createScheduleRequestFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            scheduleDescription: expect.any(Object),
            intensity: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IScheduleRequest should create a new form with FormGroup', () => {
        const formGroup = service.createScheduleRequestFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            scheduleDescription: expect.any(Object),
            intensity: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getScheduleRequest', () => {
      it('should return NewScheduleRequest for default ScheduleRequest initial value', () => {
        const formGroup = service.createScheduleRequestFormGroup(sampleWithNewData);

        const scheduleRequest = service.getScheduleRequest(formGroup) as any;

        expect(scheduleRequest).toMatchObject(sampleWithNewData);
      });

      it('should return NewScheduleRequest for empty ScheduleRequest initial value', () => {
        const formGroup = service.createScheduleRequestFormGroup();

        const scheduleRequest = service.getScheduleRequest(formGroup) as any;

        expect(scheduleRequest).toMatchObject({});
      });

      it('should return IScheduleRequest', () => {
        const formGroup = service.createScheduleRequestFormGroup(sampleWithRequiredData);

        const scheduleRequest = service.getScheduleRequest(formGroup) as any;

        expect(scheduleRequest).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IScheduleRequest should not enable id FormControl', () => {
        const formGroup = service.createScheduleRequestFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewScheduleRequest should disable id FormControl', () => {
        const formGroup = service.createScheduleRequestFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
