import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { FriendshipDetailComponent } from './friendship-detail.component';

describe('Friendship Management Detail Component', () => {
  let comp: FriendshipDetailComponent;
  let fixture: ComponentFixture<FriendshipDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FriendshipDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./friendship-detail.component').then(m => m.FriendshipDetailComponent),
              resolve: { friendship: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(FriendshipDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FriendshipDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load friendship on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FriendshipDetailComponent);

      // THEN
      expect(instance.friendship()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
