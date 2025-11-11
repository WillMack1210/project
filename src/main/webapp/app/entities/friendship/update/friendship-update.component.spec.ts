import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { FriendshipService } from '../service/friendship.service';
import { IFriendship } from '../friendship.model';
import { FriendshipFormService } from './friendship-form.service';

import { FriendshipUpdateComponent } from './friendship-update.component';

describe('Friendship Management Update Component', () => {
  let comp: FriendshipUpdateComponent;
  let fixture: ComponentFixture<FriendshipUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let friendshipFormService: FriendshipFormService;
  let friendshipService: FriendshipService;
  let userProfileService: UserProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FriendshipUpdateComponent],
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
      .overrideTemplate(FriendshipUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FriendshipUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    friendshipFormService = TestBed.inject(FriendshipFormService);
    friendshipService = TestBed.inject(FriendshipService);
    userProfileService = TestBed.inject(UserProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserProfile query and add missing value', () => {
      const friendship: IFriendship = { id: 456 };
      const user: IUserProfile = { id: 28641 };
      friendship.user = user;
      const friend: IUserProfile = { id: 2109 };
      friendship.friend = friend;

      const userProfileCollection: IUserProfile[] = [{ id: 9895 }];
      jest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [user, friend];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      jest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(expect.objectContaining),
      );
      expect(comp.userProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const friendship: IFriendship = { id: 456 };
      const user: IUserProfile = { id: 7599 };
      friendship.user = user;
      const friend: IUserProfile = { id: 24534 };
      friendship.friend = friend;

      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      expect(comp.userProfilesSharedCollection).toContain(user);
      expect(comp.userProfilesSharedCollection).toContain(friend);
      expect(comp.friendship).toEqual(friendship);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendship>>();
      const friendship = { id: 123 };
      jest.spyOn(friendshipFormService, 'getFriendship').mockReturnValue(friendship);
      jest.spyOn(friendshipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: friendship }));
      saveSubject.complete();

      // THEN
      expect(friendshipFormService.getFriendship).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(friendshipService.update).toHaveBeenCalledWith(expect.objectContaining(friendship));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendship>>();
      const friendship = { id: 123 };
      jest.spyOn(friendshipFormService, 'getFriendship').mockReturnValue({ id: null });
      jest.spyOn(friendshipService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendship: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: friendship }));
      saveSubject.complete();

      // THEN
      expect(friendshipFormService.getFriendship).toHaveBeenCalled();
      expect(friendshipService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendship>>();
      const friendship = { id: 123 };
      jest.spyOn(friendshipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(friendshipService.update).toHaveBeenCalled();
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
