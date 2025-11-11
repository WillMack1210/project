import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import FriendshipResolve from './route/friendship-routing-resolve.service';

const friendshipRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/friendship.component').then(m => m.FriendshipComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/friendship-detail.component').then(m => m.FriendshipDetailComponent),
    resolve: {
      friendship: FriendshipResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/friendship-update.component').then(m => m.FriendshipUpdateComponent),
    resolve: {
      friendship: FriendshipResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/friendship-update.component').then(m => m.FriendshipUpdateComponent),
    resolve: {
      friendship: FriendshipResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default friendshipRoute;
