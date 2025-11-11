import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISuggestion, NewSuggestion } from '../suggestion.model';

export type PartialUpdateSuggestion = Partial<ISuggestion> & Pick<ISuggestion, 'id'>;

type RestOf<T extends ISuggestion | NewSuggestion> = Omit<T, 'suggestedStart' | 'suggestedEnd'> & {
  suggestedStart?: string | null;
  suggestedEnd?: string | null;
};

export type RestSuggestion = RestOf<ISuggestion>;

export type NewRestSuggestion = RestOf<NewSuggestion>;

export type PartialUpdateRestSuggestion = RestOf<PartialUpdateSuggestion>;

export type EntityResponseType = HttpResponse<ISuggestion>;
export type EntityArrayResponseType = HttpResponse<ISuggestion[]>;

@Injectable({ providedIn: 'root' })
export class SuggestionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/suggestions');

  create(suggestion: NewSuggestion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(suggestion);
    return this.http
      .post<RestSuggestion>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(suggestion: ISuggestion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(suggestion);
    return this.http
      .put<RestSuggestion>(`${this.resourceUrl}/${this.getSuggestionIdentifier(suggestion)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(suggestion: PartialUpdateSuggestion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(suggestion);
    return this.http
      .patch<RestSuggestion>(`${this.resourceUrl}/${this.getSuggestionIdentifier(suggestion)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSuggestion>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSuggestion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSuggestionIdentifier(suggestion: Pick<ISuggestion, 'id'>): number {
    return suggestion.id;
  }

  compareSuggestion(o1: Pick<ISuggestion, 'id'> | null, o2: Pick<ISuggestion, 'id'> | null): boolean {
    return o1 && o2 ? this.getSuggestionIdentifier(o1) === this.getSuggestionIdentifier(o2) : o1 === o2;
  }

  addSuggestionToCollectionIfMissing<Type extends Pick<ISuggestion, 'id'>>(
    suggestionCollection: Type[],
    ...suggestionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const suggestions: Type[] = suggestionsToCheck.filter(isPresent);
    if (suggestions.length > 0) {
      const suggestionCollectionIdentifiers = suggestionCollection.map(suggestionItem => this.getSuggestionIdentifier(suggestionItem));
      const suggestionsToAdd = suggestions.filter(suggestionItem => {
        const suggestionIdentifier = this.getSuggestionIdentifier(suggestionItem);
        if (suggestionCollectionIdentifiers.includes(suggestionIdentifier)) {
          return false;
        }
        suggestionCollectionIdentifiers.push(suggestionIdentifier);
        return true;
      });
      return [...suggestionsToAdd, ...suggestionCollection];
    }
    return suggestionCollection;
  }

  protected convertDateFromClient<T extends ISuggestion | NewSuggestion | PartialUpdateSuggestion>(suggestion: T): RestOf<T> {
    return {
      ...suggestion,
      suggestedStart: suggestion.suggestedStart?.toJSON() ?? null,
      suggestedEnd: suggestion.suggestedEnd?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSuggestion: RestSuggestion): ISuggestion {
    return {
      ...restSuggestion,
      suggestedStart: restSuggestion.suggestedStart ? dayjs(restSuggestion.suggestedStart) : undefined,
      suggestedEnd: restSuggestion.suggestedEnd ? dayjs(restSuggestion.suggestedEnd) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSuggestion>): HttpResponse<ISuggestion> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSuggestion[]>): HttpResponse<ISuggestion[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
