import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IScheduleRequest } from '../schedule-request.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../schedule-request.test-samples';

import { RestScheduleRequest, ScheduleRequestService } from './schedule-request.service';

const requireRestSample: RestScheduleRequest = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.toJSON(),
  endDate: sampleWithRequiredData.endDate?.toJSON(),
};

describe('ScheduleRequest Service', () => {
  let service: ScheduleRequestService;
  let httpMock: HttpTestingController;
  let expectedResult: IScheduleRequest | IScheduleRequest[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ScheduleRequestService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ScheduleRequest', () => {
      const scheduleRequest = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(scheduleRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ScheduleRequest', () => {
      const scheduleRequest = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(scheduleRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ScheduleRequest', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ScheduleRequest', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ScheduleRequest', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addScheduleRequestToCollectionIfMissing', () => {
      it('should add a ScheduleRequest to an empty array', () => {
        const scheduleRequest: IScheduleRequest = sampleWithRequiredData;
        expectedResult = service.addScheduleRequestToCollectionIfMissing([], scheduleRequest);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scheduleRequest);
      });

      it('should not add a ScheduleRequest to an array that contains it', () => {
        const scheduleRequest: IScheduleRequest = sampleWithRequiredData;
        const scheduleRequestCollection: IScheduleRequest[] = [
          {
            ...scheduleRequest,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScheduleRequestToCollectionIfMissing(scheduleRequestCollection, scheduleRequest);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ScheduleRequest to an array that doesn't contain it", () => {
        const scheduleRequest: IScheduleRequest = sampleWithRequiredData;
        const scheduleRequestCollection: IScheduleRequest[] = [sampleWithPartialData];
        expectedResult = service.addScheduleRequestToCollectionIfMissing(scheduleRequestCollection, scheduleRequest);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scheduleRequest);
      });

      it('should add only unique ScheduleRequest to an array', () => {
        const scheduleRequestArray: IScheduleRequest[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const scheduleRequestCollection: IScheduleRequest[] = [sampleWithRequiredData];
        expectedResult = service.addScheduleRequestToCollectionIfMissing(scheduleRequestCollection, ...scheduleRequestArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scheduleRequest: IScheduleRequest = sampleWithRequiredData;
        const scheduleRequest2: IScheduleRequest = sampleWithPartialData;
        expectedResult = service.addScheduleRequestToCollectionIfMissing([], scheduleRequest, scheduleRequest2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scheduleRequest);
        expect(expectedResult).toContain(scheduleRequest2);
      });

      it('should accept null and undefined values', () => {
        const scheduleRequest: IScheduleRequest = sampleWithRequiredData;
        expectedResult = service.addScheduleRequestToCollectionIfMissing([], null, scheduleRequest, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scheduleRequest);
      });

      it('should return initial array if no ScheduleRequest is added', () => {
        const scheduleRequestCollection: IScheduleRequest[] = [sampleWithRequiredData];
        expectedResult = service.addScheduleRequestToCollectionIfMissing(scheduleRequestCollection, undefined, null);
        expect(expectedResult).toEqual(scheduleRequestCollection);
      });
    });

    describe('compareScheduleRequest', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScheduleRequest(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareScheduleRequest(entity1, entity2);
        const compareResult2 = service.compareScheduleRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareScheduleRequest(entity1, entity2);
        const compareResult2 = service.compareScheduleRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareScheduleRequest(entity1, entity2);
        const compareResult2 = service.compareScheduleRequest(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
