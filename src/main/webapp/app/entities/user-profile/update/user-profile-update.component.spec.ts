import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IFindTime } from 'app/entities/find-time/find-time.model';
import { FindTimeService } from 'app/entities/find-time/service/find-time.service';
import { IUserProfile } from '../user-profile.model';
import { UserProfileService } from '../service/user-profile.service';
import { UserProfileFormService } from './user-profile-form.service';

import { UserProfileUpdateComponent } from './user-profile-update.component';

describe('UserProfile Management Update Component', () => {
  let comp: UserProfileUpdateComponent;
  let fixture: ComponentFixture<UserProfileUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userProfileFormService: UserProfileFormService;
  let userProfileService: UserProfileService;
  let userService: UserService;
  let eventService: EventService;
  let findTimeService: FindTimeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UserProfileUpdateComponent],
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
      .overrideTemplate(UserProfileUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserProfileUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userProfileFormService = TestBed.inject(UserProfileFormService);
    userProfileService = TestBed.inject(UserProfileService);
    userService = TestBed.inject(UserService);
    eventService = TestBed.inject(EventService);
    findTimeService = TestBed.inject(FindTimeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const userProfile: IUserProfile = { id: 456 };
      const user: IUser = { id: 28624 };
      userProfile.user = user;

      const userCollection: IUser[] = [{ id: 7826 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userProfile });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Event query and add missing value', () => {
      const userProfile: IUserProfile = { id: 456 };
      const sharedEvents: IEvent[] = [{ id: 21945 }];
      userProfile.sharedEvents = sharedEvents;

      const eventCollection: IEvent[] = [{ id: 18981 }];
      jest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [...sharedEvents];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      jest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userProfile });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(expect.objectContaining),
      );
      expect(comp.eventsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call FindTime query and add missing value', () => {
      const userProfile: IUserProfile = { id: 456 };
      const findTimes: IFindTime[] = [{ id: 10156 }];
      userProfile.findTimes = findTimes;

      const findTimeCollection: IFindTime[] = [{ id: 28091 }];
      jest.spyOn(findTimeService, 'query').mockReturnValue(of(new HttpResponse({ body: findTimeCollection })));
      const additionalFindTimes = [...findTimes];
      const expectedCollection: IFindTime[] = [...additionalFindTimes, ...findTimeCollection];
      jest.spyOn(findTimeService, 'addFindTimeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userProfile });
      comp.ngOnInit();

      expect(findTimeService.query).toHaveBeenCalled();
      expect(findTimeService.addFindTimeToCollectionIfMissing).toHaveBeenCalledWith(
        findTimeCollection,
        ...additionalFindTimes.map(expect.objectContaining),
      );
      expect(comp.findTimesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userProfile: IUserProfile = { id: 456 };
      const user: IUser = { id: 25293 };
      userProfile.user = user;
      const sharedEvent: IEvent = { id: 32415 };
      userProfile.sharedEvents = [sharedEvent];
      const findTime: IFindTime = { id: 28981 };
      userProfile.findTimes = [findTime];

      activatedRoute.data = of({ userProfile });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.eventsSharedCollection).toContain(sharedEvent);
      expect(comp.findTimesSharedCollection).toContain(findTime);
      expect(comp.userProfile).toEqual(userProfile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserProfile>>();
      const userProfile = { id: 123 };
      jest.spyOn(userProfileFormService, 'getUserProfile').mockReturnValue(userProfile);
      jest.spyOn(userProfileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userProfile }));
      saveSubject.complete();

      // THEN
      expect(userProfileFormService.getUserProfile).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userProfileService.update).toHaveBeenCalledWith(expect.objectContaining(userProfile));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserProfile>>();
      const userProfile = { id: 123 };
      jest.spyOn(userProfileFormService, 'getUserProfile').mockReturnValue({ id: null });
      jest.spyOn(userProfileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userProfile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userProfile }));
      saveSubject.complete();

      // THEN
      expect(userProfileFormService.getUserProfile).toHaveBeenCalled();
      expect(userProfileService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserProfile>>();
      const userProfile = { id: 123 };
      jest.spyOn(userProfileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userProfileService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEvent', () => {
      it('Should forward to eventService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(eventService, 'compareEvent');
        comp.compareEvent(entity, entity2);
        expect(eventService.compareEvent).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
