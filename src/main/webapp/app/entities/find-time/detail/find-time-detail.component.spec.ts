import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { FindTimeDetailComponent } from './find-time-detail.component';

describe('FindTime Management Detail Component', () => {
  let comp: FindTimeDetailComponent;
  let fixture: ComponentFixture<FindTimeDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FindTimeDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./find-time-detail.component').then(m => m.FindTimeDetailComponent),
              resolve: { findTime: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(FindTimeDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FindTimeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load findTime on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FindTimeDetailComponent);

      // THEN
      expect(instance.findTime()).toEqual(expect.objectContaining({ id: 123 }));
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
