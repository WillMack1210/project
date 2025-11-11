import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFindTime } from '../find-time.model';
import { FindTimeService } from '../service/find-time.service';

const findTimeResolve = (route: ActivatedRouteSnapshot): Observable<null | IFindTime> => {
  const id = route.params.id;
  if (id) {
    return inject(FindTimeService)
      .find(id)
      .pipe(
        mergeMap((findTime: HttpResponse<IFindTime>) => {
          if (findTime.body) {
            return of(findTime.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default findTimeResolve;
