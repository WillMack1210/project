import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISuggestion } from '../suggestion.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../suggestion.test-samples';

import { RestSuggestion, SuggestionService } from './suggestion.service';

const requireRestSample: RestSuggestion = {
  ...sampleWithRequiredData,
  suggestedStart: sampleWithRequiredData.suggestedStart?.toJSON(),
  suggestedEnd: sampleWithRequiredData.suggestedEnd?.toJSON(),
};

describe('Suggestion Service', () => {
  let service: SuggestionService;
  let httpMock: HttpTestingController;
  let expectedResult: ISuggestion | ISuggestion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SuggestionService);
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

    it('should create a Suggestion', () => {
      const suggestion = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(suggestion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Suggestion', () => {
      const suggestion = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(suggestion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Suggestion', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Suggestion', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Suggestion', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSuggestionToCollectionIfMissing', () => {
      it('should add a Suggestion to an empty array', () => {
        const suggestion: ISuggestion = sampleWithRequiredData;
        expectedResult = service.addSuggestionToCollectionIfMissing([], suggestion);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(suggestion);
      });

      it('should not add a Suggestion to an array that contains it', () => {
        const suggestion: ISuggestion = sampleWithRequiredData;
        const suggestionCollection: ISuggestion[] = [
          {
            ...suggestion,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSuggestionToCollectionIfMissing(suggestionCollection, suggestion);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Suggestion to an array that doesn't contain it", () => {
        const suggestion: ISuggestion = sampleWithRequiredData;
        const suggestionCollection: ISuggestion[] = [sampleWithPartialData];
        expectedResult = service.addSuggestionToCollectionIfMissing(suggestionCollection, suggestion);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(suggestion);
      });

      it('should add only unique Suggestion to an array', () => {
        const suggestionArray: ISuggestion[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const suggestionCollection: ISuggestion[] = [sampleWithRequiredData];
        expectedResult = service.addSuggestionToCollectionIfMissing(suggestionCollection, ...suggestionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const suggestion: ISuggestion = sampleWithRequiredData;
        const suggestion2: ISuggestion = sampleWithPartialData;
        expectedResult = service.addSuggestionToCollectionIfMissing([], suggestion, suggestion2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(suggestion);
        expect(expectedResult).toContain(suggestion2);
      });

      it('should accept null and undefined values', () => {
        const suggestion: ISuggestion = sampleWithRequiredData;
        expectedResult = service.addSuggestionToCollectionIfMissing([], null, suggestion, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(suggestion);
      });

      it('should return initial array if no Suggestion is added', () => {
        const suggestionCollection: ISuggestion[] = [sampleWithRequiredData];
        expectedResult = service.addSuggestionToCollectionIfMissing(suggestionCollection, undefined, null);
        expect(expectedResult).toEqual(suggestionCollection);
      });
    });

    describe('compareSuggestion', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSuggestion(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSuggestion(entity1, entity2);
        const compareResult2 = service.compareSuggestion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSuggestion(entity1, entity2);
        const compareResult2 = service.compareSuggestion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSuggestion(entity1, entity2);
        const compareResult2 = service.compareSuggestion(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
