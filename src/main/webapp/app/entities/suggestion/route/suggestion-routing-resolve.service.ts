import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISuggestion } from '../suggestion.model';
import { SuggestionService } from '../service/suggestion.service';

const suggestionResolve = (route: ActivatedRouteSnapshot): Observable<null | ISuggestion> => {
  const id = route.params.id;
  if (id) {
    return inject(SuggestionService)
      .find(id)
      .pipe(
        mergeMap((suggestion: HttpResponse<ISuggestion>) => {
          if (suggestion.body) {
            return of(suggestion.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default suggestionResolve;
