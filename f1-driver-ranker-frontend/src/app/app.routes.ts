import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'compare' },
  {
    path: 'compare',
    loadComponent: () => import('./features/compare/compare.page').then(m => m.ComparePage)
  },
  { path: '**', redirectTo: 'compare' }
];
