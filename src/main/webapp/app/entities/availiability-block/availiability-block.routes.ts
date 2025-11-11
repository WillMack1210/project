import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import AvailiabilityBlockResolve from './route/availiability-block-routing-resolve.service';

const availiabilityBlockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/availiability-block.component').then(m => m.AvailiabilityBlockComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/availiability-block-detail.component').then(m => m.AvailiabilityBlockDetailComponent),
    resolve: {
      availiabilityBlock: AvailiabilityBlockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/availiability-block-update.component').then(m => m.AvailiabilityBlockUpdateComponent),
    resolve: {
      availiabilityBlock: AvailiabilityBlockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/availiability-block-update.component').then(m => m.AvailiabilityBlockUpdateComponent),
    resolve: {
      availiabilityBlock: AvailiabilityBlockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default availiabilityBlockRoute;
