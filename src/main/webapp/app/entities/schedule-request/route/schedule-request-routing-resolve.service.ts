import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IScheduleRequest } from '../schedule-request.model';
import { ScheduleRequestService } from '../service/schedule-request.service';

const scheduleRequestResolve = (route: ActivatedRouteSnapshot): Observable<null | IScheduleRequest> => {
  const id = route.params.id;
  if (id) {
    return inject(ScheduleRequestService)
      .find(id)
      .pipe(
        mergeMap((scheduleRequest: HttpResponse<IScheduleRequest>) => {
          if (scheduleRequest.body) {
            return of(scheduleRequest.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default scheduleRequestResolve;
