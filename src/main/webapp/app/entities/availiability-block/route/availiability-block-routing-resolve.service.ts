import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAvailiabilityBlock } from '../availiability-block.model';
import { AvailiabilityBlockService } from '../service/availiability-block.service';

const availiabilityBlockResolve = (route: ActivatedRouteSnapshot): Observable<null | IAvailiabilityBlock> => {
  const id = route.params.id;
  if (id) {
    return inject(AvailiabilityBlockService)
      .find(id)
      .pipe(
        mergeMap((availiabilityBlock: HttpResponse<IAvailiabilityBlock>) => {
          if (availiabilityBlock.body) {
            return of(availiabilityBlock.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default availiabilityBlockResolve;
