import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { ISuggestion } from 'app/entities/suggestion/suggestion.model';
import { SuggestionService } from 'app/entities/suggestion/service/suggestion.service';
import { IFindTime } from '../find-time.model';
import { FindTimeService } from '../service/find-time.service';
import { FindTimeFormService } from './find-time-form.service';

import { FindTimeUpdateComponent } from './find-time-update.component';

describe('FindTime Management Update Component', () => {
  let comp: FindTimeUpdateComponent;
  let fixture: ComponentFixture<FindTimeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let findTimeFormService: FindTimeFormService;
  let findTimeService: FindTimeService;
  let userProfileService: UserProfileService;
  let suggestionService: SuggestionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FindTimeUpdateComponent],
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
      .overrideTemplate(FindTimeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FindTimeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    findTimeFormService = TestBed.inject(FindTimeFormService);
    findTimeService = TestBed.inject(FindTimeService);
    userProfileService = TestBed.inject(UserProfileService);
    suggestionService = TestBed.inject(SuggestionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserProfile query and add missing value', () => {
      const findTime: IFindTime = { id: 456 };
      const requester: IUserProfile = { id: 25081 };
      findTime.requester = requester;
      const participants: IUserProfile[] = [{ id: 29427 }];
      findTime.participants = participants;

      const userProfileCollection: IUserProfile[] = [{ id: 14446 }];
      jest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [requester, ...participants];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      jest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ findTime });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(expect.objectContaining),
      );
      expect(comp.userProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Suggestion query and add missing value', () => {
      const findTime: IFindTime = { id: 456 };
      const suggestions: ISuggestion[] = [{ id: 18537 }];
      findTime.suggestions = suggestions;

      const suggestionCollection: ISuggestion[] = [{ id: 12859 }];
      jest.spyOn(suggestionService, 'query').mockReturnValue(of(new HttpResponse({ body: suggestionCollection })));
      const additionalSuggestions = [...suggestions];
      const expectedCollection: ISuggestion[] = [...additionalSuggestions, ...suggestionCollection];
      jest.spyOn(suggestionService, 'addSuggestionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ findTime });
      comp.ngOnInit();

      expect(suggestionService.query).toHaveBeenCalled();
      expect(suggestionService.addSuggestionToCollectionIfMissing).toHaveBeenCalledWith(
        suggestionCollection,
        ...additionalSuggestions.map(expect.objectContaining),
      );
      expect(comp.suggestionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const findTime: IFindTime = { id: 456 };
      const requester: IUserProfile = { id: 31597 };
      findTime.requester = requester;
      const participant: IUserProfile = { id: 21139 };
      findTime.participants = [participant];
      const suggestion: ISuggestion = { id: 8769 };
      findTime.suggestions = [suggestion];

      activatedRoute.data = of({ findTime });
      comp.ngOnInit();

      expect(comp.userProfilesSharedCollection).toContain(requester);
      expect(comp.userProfilesSharedCollection).toContain(participant);
      expect(comp.suggestionsSharedCollection).toContain(suggestion);
      expect(comp.findTime).toEqual(findTime);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFindTime>>();
      const findTime = { id: 123 };
      jest.spyOn(findTimeFormService, 'getFindTime').mockReturnValue(findTime);
      jest.spyOn(findTimeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ findTime });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: findTime }));
      saveSubject.complete();

      // THEN
      expect(findTimeFormService.getFindTime).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(findTimeService.update).toHaveBeenCalledWith(expect.objectContaining(findTime));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFindTime>>();
      const findTime = { id: 123 };
      jest.spyOn(findTimeFormService, 'getFindTime').mockReturnValue({ id: null });
      jest.spyOn(findTimeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ findTime: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: findTime }));
      saveSubject.complete();

      // THEN
      expect(findTimeFormService.getFindTime).toHaveBeenCalled();
      expect(findTimeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFindTime>>();
      const findTime = { id: 123 };
      jest.spyOn(findTimeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ findTime });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(findTimeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserProfile', () => {
      it('Should forward to userProfileService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userProfileService, 'compareUserProfile');
        comp.compareUserProfile(entity, entity2);
        expect(userProfileService.compareUserProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSuggestion', () => {
      it('Should forward to suggestionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(suggestionService, 'compareSuggestion');
        comp.compareSuggestion(entity, entity2);
        expect(suggestionService.compareSuggestion).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
