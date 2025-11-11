import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IFindTime } from '../find-time.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../find-time.test-samples';

import { FindTimeService, RestFindTime } from './find-time.service';

const requireRestSample: RestFindTime = {
  ...sampleWithRequiredData,
  requestStart: sampleWithRequiredData.requestStart?.toJSON(),
  requestEnd: sampleWithRequiredData.requestEnd?.toJSON(),
};

describe('FindTime Service', () => {
  let service: FindTimeService;
  let httpMock: HttpTestingController;
  let expectedResult: IFindTime | IFindTime[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FindTimeService);
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

    it('should create a FindTime', () => {
      const findTime = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(findTime).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FindTime', () => {
      const findTime = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(findTime).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FindTime', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FindTime', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FindTime', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFindTimeToCollectionIfMissing', () => {
      it('should add a FindTime to an empty array', () => {
        const findTime: IFindTime = sampleWithRequiredData;
        expectedResult = service.addFindTimeToCollectionIfMissing([], findTime);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(findTime);
      });

      it('should not add a FindTime to an array that contains it', () => {
        const findTime: IFindTime = sampleWithRequiredData;
        const findTimeCollection: IFindTime[] = [
          {
            ...findTime,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFindTimeToCollectionIfMissing(findTimeCollection, findTime);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FindTime to an array that doesn't contain it", () => {
        const findTime: IFindTime = sampleWithRequiredData;
        const findTimeCollection: IFindTime[] = [sampleWithPartialData];
        expectedResult = service.addFindTimeToCollectionIfMissing(findTimeCollection, findTime);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(findTime);
      });

      it('should add only unique FindTime to an array', () => {
        const findTimeArray: IFindTime[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const findTimeCollection: IFindTime[] = [sampleWithRequiredData];
        expectedResult = service.addFindTimeToCollectionIfMissing(findTimeCollection, ...findTimeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const findTime: IFindTime = sampleWithRequiredData;
        const findTime2: IFindTime = sampleWithPartialData;
        expectedResult = service.addFindTimeToCollectionIfMissing([], findTime, findTime2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(findTime);
        expect(expectedResult).toContain(findTime2);
      });

      it('should accept null and undefined values', () => {
        const findTime: IFindTime = sampleWithRequiredData;
        expectedResult = service.addFindTimeToCollectionIfMissing([], null, findTime, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(findTime);
      });

      it('should return initial array if no FindTime is added', () => {
        const findTimeCollection: IFindTime[] = [sampleWithRequiredData];
        expectedResult = service.addFindTimeToCollectionIfMissing(findTimeCollection, undefined, null);
        expect(expectedResult).toEqual(findTimeCollection);
      });
    });

    describe('compareFindTime', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFindTime(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareFindTime(entity1, entity2);
        const compareResult2 = service.compareFindTime(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareFindTime(entity1, entity2);
        const compareResult2 = service.compareFindTime(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareFindTime(entity1, entity2);
        const compareResult2 = service.compareFindTime(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
