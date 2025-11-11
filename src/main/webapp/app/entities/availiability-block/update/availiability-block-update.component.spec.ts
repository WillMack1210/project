import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { AvailiabilityBlockService } from '../service/availiability-block.service';
import { IAvailiabilityBlock } from '../availiability-block.model';
import { AvailiabilityBlockFormService } from './availiability-block-form.service';

import { AvailiabilityBlockUpdateComponent } from './availiability-block-update.component';

describe('AvailiabilityBlock Management Update Component', () => {
  let comp: AvailiabilityBlockUpdateComponent;
  let fixture: ComponentFixture<AvailiabilityBlockUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let availiabilityBlockFormService: AvailiabilityBlockFormService;
  let availiabilityBlockService: AvailiabilityBlockService;
  let userProfileService: UserProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AvailiabilityBlockUpdateComponent],
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
      .overrideTemplate(AvailiabilityBlockUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AvailiabilityBlockUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    availiabilityBlockFormService = TestBed.inject(AvailiabilityBlockFormService);
    availiabilityBlockService = TestBed.inject(AvailiabilityBlockService);
    userProfileService = TestBed.inject(UserProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserProfile query and add missing value', () => {
      const availiabilityBlock: IAvailiabilityBlock = { id: 456 };
      const user: IUserProfile = { id: 17497 };
      availiabilityBlock.user = user;

      const userProfileCollection: IUserProfile[] = [{ id: 21365 }];
      jest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [user];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      jest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ availiabilityBlock });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(expect.objectContaining),
      );
      expect(comp.userProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const availiabilityBlock: IAvailiabilityBlock = { id: 456 };
      const user: IUserProfile = { id: 23768 };
      availiabilityBlock.user = user;

      activatedRoute.data = of({ availiabilityBlock });
      comp.ngOnInit();

      expect(comp.userProfilesSharedCollection).toContain(user);
      expect(comp.availiabilityBlock).toEqual(availiabilityBlock);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAvailiabilityBlock>>();
      const availiabilityBlock = { id: 123 };
      jest.spyOn(availiabilityBlockFormService, 'getAvailiabilityBlock').mockReturnValue(availiabilityBlock);
      jest.spyOn(availiabilityBlockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ availiabilityBlock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: availiabilityBlock }));
      saveSubject.complete();

      // THEN
      expect(availiabilityBlockFormService.getAvailiabilityBlock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(availiabilityBlockService.update).toHaveBeenCalledWith(expect.objectContaining(availiabilityBlock));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAvailiabilityBlock>>();
      const availiabilityBlock = { id: 123 };
      jest.spyOn(availiabilityBlockFormService, 'getAvailiabilityBlock').mockReturnValue({ id: null });
      jest.spyOn(availiabilityBlockService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ availiabilityBlock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: availiabilityBlock }));
      saveSubject.complete();

      // THEN
      expect(availiabilityBlockFormService.getAvailiabilityBlock).toHaveBeenCalled();
      expect(availiabilityBlockService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAvailiabilityBlock>>();
      const availiabilityBlock = { id: 123 };
      jest.spyOn(availiabilityBlockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ availiabilityBlock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(availiabilityBlockService.update).toHaveBeenCalled();
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
