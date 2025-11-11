import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IFindTime } from 'app/entities/find-time/find-time.model';
import { FindTimeService } from 'app/entities/find-time/service/find-time.service';
import { SuggestionService } from '../service/suggestion.service';
import { ISuggestion } from '../suggestion.model';
import { SuggestionFormService } from './suggestion-form.service';

import { SuggestionUpdateComponent } from './suggestion-update.component';

describe('Suggestion Management Update Component', () => {
  let comp: SuggestionUpdateComponent;
  let fixture: ComponentFixture<SuggestionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let suggestionFormService: SuggestionFormService;
  let suggestionService: SuggestionService;
  let findTimeService: FindTimeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SuggestionUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SuggestionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SuggestionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    suggestionFormService = TestBed.inject(SuggestionFormService);
    suggestionService = TestBed.inject(SuggestionService);
    findTimeService = TestBed.inject(FindTimeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call FindTime query and add missing value', () => {
      const suggestion: ISuggestion = { id: 456 };
      const findTimes: IFindTime[] = [{ id: 7025 }];
      suggestion.findTimes = findTimes;

      const findTimeCollection: IFindTime[] = [{ id: 794 }];
      jest.spyOn(findTimeService, 'query').mockReturnValue(of(new HttpResponse({ body: findTimeCollection })));
      const additionalFindTimes = [...findTimes];
      const expectedCollection: IFindTime[] = [...additionalFindTimes, ...findTimeCollection];
      jest.spyOn(findTimeService, 'addFindTimeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ suggestion });
      comp.ngOnInit();

      expect(findTimeService.query).toHaveBeenCalled();
      expect(findTimeService.addFindTimeToCollectionIfMissing).toHaveBeenCalledWith(
        findTimeCollection,
        ...additionalFindTimes.map(expect.objectContaining),
      );
      expect(comp.findTimesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const suggestion: ISuggestion = { id: 456 };
      const findTime: IFindTime = { id: 2209 };
      suggestion.findTimes = [findTime];

      activatedRoute.data = of({ suggestion });
      comp.ngOnInit();

      expect(comp.findTimesSharedCollection).toContain(findTime);
      expect(comp.suggestion).toEqual(suggestion);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISuggestion>>();
      const suggestion = { id: 123 };
      jest.spyOn(suggestionFormService, 'getSuggestion').mockReturnValue(suggestion);
      jest.spyOn(suggestionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ suggestion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: suggestion }));
      saveSubject.complete();

      // THEN
      expect(suggestionFormService.getSuggestion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(suggestionService.update).toHaveBeenCalledWith(expect.objectContaining(suggestion));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISuggestion>>();
      const suggestion = { id: 123 };
      jest.spyOn(suggestionFormService, 'getSuggestion').mockReturnValue({ id: null });
      jest.spyOn(suggestionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ suggestion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: suggestion }));
      saveSubject.complete();

      // THEN
      expect(suggestionFormService.getSuggestion).toHaveBeenCalled();
      expect(suggestionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISuggestion>>();
      const suggestion = { id: 123 };
      jest.spyOn(suggestionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ suggestion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(suggestionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFindTime', () => {
      it('Should forward to findTimeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(findTimeService, 'compareFindTime');
        comp.compareFindTime(entity, entity2);
        expect(findTimeService.compareFindTime).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
