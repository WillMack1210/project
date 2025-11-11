import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import FindTimeResolve from './route/find-time-routing-resolve.service';

const findTimeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/find-time.component').then(m => m.FindTimeComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/find-time-detail.component').then(m => m.FindTimeDetailComponent),
    resolve: {
      findTime: FindTimeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/find-time-update.component').then(m => m.FindTimeUpdateComponent),
    resolve: {
      findTime: FindTimeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/find-time-update.component').then(m => m.FindTimeUpdateComponent),
    resolve: {
      findTime: FindTimeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default findTimeRoute;
