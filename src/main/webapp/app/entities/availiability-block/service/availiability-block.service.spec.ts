import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IAvailiabilityBlock } from '../availiability-block.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../availiability-block.test-samples';

import { AvailiabilityBlockService, RestAvailiabilityBlock } from './availiability-block.service';

const requireRestSample: RestAvailiabilityBlock = {
  ...sampleWithRequiredData,
  startDateTime: sampleWithRequiredData.startDateTime?.toJSON(),
  endDateTime: sampleWithRequiredData.endDateTime?.toJSON(),
};

describe('AvailiabilityBlock Service', () => {
  let service: AvailiabilityBlockService;
  let httpMock: HttpTestingController;
  let expectedResult: IAvailiabilityBlock | IAvailiabilityBlock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AvailiabilityBlockService);
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

    it('should create a AvailiabilityBlock', () => {
      const availiabilityBlock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(availiabilityBlock).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AvailiabilityBlock', () => {
      const availiabilityBlock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(availiabilityBlock).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AvailiabilityBlock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AvailiabilityBlock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a AvailiabilityBlock', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAvailiabilityBlockToCollectionIfMissing', () => {
      it('should add a AvailiabilityBlock to an empty array', () => {
        const availiabilityBlock: IAvailiabilityBlock = sampleWithRequiredData;
        expectedResult = service.addAvailiabilityBlockToCollectionIfMissing([], availiabilityBlock);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(availiabilityBlock);
      });

      it('should not add a AvailiabilityBlock to an array that contains it', () => {
        const availiabilityBlock: IAvailiabilityBlock = sampleWithRequiredData;
        const availiabilityBlockCollection: IAvailiabilityBlock[] = [
          {
            ...availiabilityBlock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAvailiabilityBlockToCollectionIfMissing(availiabilityBlockCollection, availiabilityBlock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AvailiabilityBlock to an array that doesn't contain it", () => {
        const availiabilityBlock: IAvailiabilityBlock = sampleWithRequiredData;
        const availiabilityBlockCollection: IAvailiabilityBlock[] = [sampleWithPartialData];
        expectedResult = service.addAvailiabilityBlockToCollectionIfMissing(availiabilityBlockCollection, availiabilityBlock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(availiabilityBlock);
      });

      it('should add only unique AvailiabilityBlock to an array', () => {
        const availiabilityBlockArray: IAvailiabilityBlock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const availiabilityBlockCollection: IAvailiabilityBlock[] = [sampleWithRequiredData];
        expectedResult = service.addAvailiabilityBlockToCollectionIfMissing(availiabilityBlockCollection, ...availiabilityBlockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const availiabilityBlock: IAvailiabilityBlock = sampleWithRequiredData;
        const availiabilityBlock2: IAvailiabilityBlock = sampleWithPartialData;
        expectedResult = service.addAvailiabilityBlockToCollectionIfMissing([], availiabilityBlock, availiabilityBlock2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(availiabilityBlock);
        expect(expectedResult).toContain(availiabilityBlock2);
      });

      it('should accept null and undefined values', () => {
        const availiabilityBlock: IAvailiabilityBlock = sampleWithRequiredData;
        expectedResult = service.addAvailiabilityBlockToCollectionIfMissing([], null, availiabilityBlock, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(availiabilityBlock);
      });

      it('should return initial array if no AvailiabilityBlock is added', () => {
        const availiabilityBlockCollection: IAvailiabilityBlock[] = [sampleWithRequiredData];
        expectedResult = service.addAvailiabilityBlockToCollectionIfMissing(availiabilityBlockCollection, undefined, null);
        expect(expectedResult).toEqual(availiabilityBlockCollection);
      });
    });

    describe('compareAvailiabilityBlock', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAvailiabilityBlock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareAvailiabilityBlock(entity1, entity2);
        const compareResult2 = service.compareAvailiabilityBlock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareAvailiabilityBlock(entity1, entity2);
        const compareResult2 = service.compareAvailiabilityBlock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareAvailiabilityBlock(entity1, entity2);
        const compareResult2 = service.compareAvailiabilityBlock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
