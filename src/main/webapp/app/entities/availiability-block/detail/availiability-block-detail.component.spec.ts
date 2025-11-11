import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { AvailiabilityBlockDetailComponent } from './availiability-block-detail.component';

describe('AvailiabilityBlock Management Detail Component', () => {
  let comp: AvailiabilityBlockDetailComponent;
  let fixture: ComponentFixture<AvailiabilityBlockDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvailiabilityBlockDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./availiability-block-detail.component').then(m => m.AvailiabilityBlockDetailComponent),
              resolve: { availiabilityBlock: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AvailiabilityBlockDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AvailiabilityBlockDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load availiabilityBlock on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AvailiabilityBlockDetailComponent);

      // THEN
      expect(instance.availiabilityBlock()).toEqual(expect.objectContaining({ id: 123 }));
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
