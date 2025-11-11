import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFriendship } from '../friendship.model';
import { FriendshipService } from '../service/friendship.service';

const friendshipResolve = (route: ActivatedRouteSnapshot): Observable<null | IFriendship> => {
  const id = route.params.id;
  if (id) {
    return inject(FriendshipService)
      .find(id)
      .pipe(
        mergeMap((friendship: HttpResponse<IFriendship>) => {
          if (friendship.body) {
            return of(friendship.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default friendshipResolve;
