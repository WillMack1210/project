import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ScheduleRequestResolve from './route/schedule-request-routing-resolve.service';

const scheduleRequestRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/schedule-request.component').then(m => m.ScheduleRequestComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/schedule-request-detail.component').then(m => m.ScheduleRequestDetailComponent),
    resolve: {
      scheduleRequest: ScheduleRequestResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/schedule-request-update.component').then(m => m.ScheduleRequestUpdateComponent),
    resolve: {
      scheduleRequest: ScheduleRequestResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/schedule-request-update.component').then(m => m.ScheduleRequestUpdateComponent),
    resolve: {
      scheduleRequest: ScheduleRequestResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default scheduleRequestRoute;
