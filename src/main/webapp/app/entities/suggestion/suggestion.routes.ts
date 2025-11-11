import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SuggestionResolve from './route/suggestion-routing-resolve.service';

const suggestionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/suggestion.component').then(m => m.SuggestionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/suggestion-detail.component').then(m => m.SuggestionDetailComponent),
    resolve: {
      suggestion: SuggestionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/suggestion-update.component').then(m => m.SuggestionUpdateComponent),
    resolve: {
      suggestion: SuggestionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/suggestion-update.component').then(m => m.SuggestionUpdateComponent),
    resolve: {
      suggestion: SuggestionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default suggestionRoute;
