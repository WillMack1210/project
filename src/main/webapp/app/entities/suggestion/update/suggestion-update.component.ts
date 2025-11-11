import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IFindTime } from 'app/entities/find-time/find-time.model';
import { FindTimeService } from 'app/entities/find-time/service/find-time.service';
import { ISuggestion } from '../suggestion.model';
import { SuggestionService } from '../service/suggestion.service';
import { SuggestionFormGroup, SuggestionFormService } from './suggestion-form.service';

@Component({
  standalone: true,
  selector: 'jhi-suggestion-update',
  templateUrl: './suggestion-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SuggestionUpdateComponent implements OnInit {
  isSaving = false;
  suggestion: ISuggestion | null = null;

  findTimesSharedCollection: IFindTime[] = [];

  protected suggestionService = inject(SuggestionService);
  protected suggestionFormService = inject(SuggestionFormService);
  protected findTimeService = inject(FindTimeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SuggestionFormGroup = this.suggestionFormService.createSuggestionFormGroup();

  compareFindTime = (o1: IFindTime | null, o2: IFindTime | null): boolean => this.findTimeService.compareFindTime(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ suggestion }) => {
      this.suggestion = suggestion;
      if (suggestion) {
        this.updateForm(suggestion);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const suggestion = this.suggestionFormService.getSuggestion(this.editForm);
    if (suggestion.id !== null) {
      this.subscribeToSaveResponse(this.suggestionService.update(suggestion));
    } else {
      this.subscribeToSaveResponse(this.suggestionService.create(suggestion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISuggestion>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(suggestion: ISuggestion): void {
    this.suggestion = suggestion;
    this.suggestionFormService.resetForm(this.editForm, suggestion);

    this.findTimesSharedCollection = this.findTimeService.addFindTimeToCollectionIfMissing<IFindTime>(
      this.findTimesSharedCollection,
      ...(suggestion.findTimes ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.findTimeService
      .query()
      .pipe(map((res: HttpResponse<IFindTime[]>) => res.body ?? []))
      .pipe(
        map((findTimes: IFindTime[]) =>
          this.findTimeService.addFindTimeToCollectionIfMissing<IFindTime>(findTimes, ...(this.suggestion?.findTimes ?? [])),
        ),
      )
      .subscribe((findTimes: IFindTime[]) => (this.findTimesSharedCollection = findTimes));
  }
}
