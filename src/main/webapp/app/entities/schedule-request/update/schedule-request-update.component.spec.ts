import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { ScheduleRequestService } from '../service/schedule-request.service';
import { IScheduleRequest } from '../schedule-request.model';
import { ScheduleRequestFormService } from './schedule-request-form.service';

import { ScheduleRequestUpdateComponent } from './schedule-request-update.component';

describe('ScheduleRequest Management Update Component', () => {
  let comp: ScheduleRequestUpdateComponent;
  let fixture: ComponentFixture<ScheduleRequestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let scheduleRequestFormService: ScheduleRequestFormService;
  let scheduleRequestService: ScheduleRequestService;
  let userProfileService: UserProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ScheduleRequestUpdateComponent],
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
      .overrideTemplate(ScheduleRequestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScheduleRequestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    scheduleRequestFormService = TestBed.inject(ScheduleRequestFormService);
    scheduleRequestService = TestBed.inject(ScheduleRequestService);
    userProfileService = TestBed.inject(UserProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserProfile query and add missing value', () => {
      const scheduleRequest: IScheduleRequest = { id: 456 };
      const user: IUserProfile = { id: 6458 };
      scheduleRequest.user = user;

      const userProfileCollection: IUserProfile[] = [{ id: 22603 }];
      jest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [user];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      jest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scheduleRequest });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(expect.objectContaining),
      );
      expect(comp.userProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const scheduleRequest: IScheduleRequest = { id: 456 };
      const user: IUserProfile = { id: 27241 };
      scheduleRequest.user = user;

      activatedRoute.data = of({ scheduleRequest });
      comp.ngOnInit();

      expect(comp.userProfilesSharedCollection).toContain(user);
      expect(comp.scheduleRequest).toEqual(scheduleRequest);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IScheduleRequest>>();
      const scheduleRequest = { id: 123 };
      jest.spyOn(scheduleRequestFormService, 'getScheduleRequest').mockReturnValue(scheduleRequest);
      jest.spyOn(scheduleRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scheduleRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: scheduleRequest }));
      saveSubject.complete();

      // THEN
      expect(scheduleRequestFormService.getScheduleRequest).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(scheduleRequestService.update).toHaveBeenCalledWith(expect.objectContaining(scheduleRequest));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IScheduleRequest>>();
      const scheduleRequest = { id: 123 };
      jest.spyOn(scheduleRequestFormService, 'getScheduleRequest').mockReturnValue({ id: null });
      jest.spyOn(scheduleRequestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scheduleRequest: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: scheduleRequest }));
      saveSubject.complete();

      // THEN
      expect(scheduleRequestFormService.getScheduleRequest).toHaveBeenCalled();
      expect(scheduleRequestService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IScheduleRequest>>();
      const scheduleRequest = { id: 123 };
      jest.spyOn(scheduleRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scheduleRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(scheduleRequestService.update).toHaveBeenCalled();
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
  });
});
