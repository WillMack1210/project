import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-profile',
    data: { pageTitle: 'UserProfiles' },
    loadChildren: () => import('./user-profile/user-profile.routes'),
  },
  {
    path: 'event',
    data: { pageTitle: 'Events' },
    loadChildren: () => import('./event/event.routes'),
  },
  {
    path: 'friendship',
    data: { pageTitle: 'Friendships' },
    loadChildren: () => import('./friendship/friendship.routes'),
  },
  {
    path: 'schedule-request',
    data: { pageTitle: 'ScheduleRequests' },
    loadChildren: () => import('./schedule-request/schedule-request.routes'),
  },
  {
    path: 'availiability-block',
    data: { pageTitle: 'AvailiabilityBlocks' },
    loadChildren: () => import('./availiability-block/availiability-block.routes'),
  },
  {
    path: 'find-time',
    data: { pageTitle: 'FindTimes' },
    loadChildren: () => import('./find-time/find-time.routes'),
  },
  {
    path: 'suggestion',
    data: { pageTitle: 'Suggestions' },
    loadChildren: () => import('./suggestion/suggestion.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
