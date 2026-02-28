import { CommonModule } from '@angular/common';
import { Component, ChangeDetectionStrategy } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { BehaviorSubject, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';

import { DriverSuggestion } from '../../core/models/driver.models';
import { CompareResponse } from '../../core/models/compare.models';
import { CompareApiService } from '../../core/api/compare-api.service';

import { ScoringModeDto } from '../../core/models/scoring-mode.models';
import { ScoringModesApiService } from '../../core/api/scoring-modes-api.service';

import { DriverMultiPickerComponent } from './components/driver-multi-picker/driver-multi-picker.component';
import { RankingTableComponent } from './components/ranking-table/ranking-table.component';

@Component({
  selector: 'app-compare-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DriverMultiPickerComponent, RankingTableComponent],
  templateUrl: './compare.page.html',
  styleUrl: './compare.page.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComparePage {
  selected: DriverSuggestion[] = [];

  readonly fromCtrl = new FormControl<number | null>(2007);
  readonly toCtrl = new FormControl<number | null>(2014);
  readonly modeCtrl = new FormControl<string>('GREATEST_BALANCED', { nonNullable: true });

  modes: ScoringModeDto[] = [];

  private readonly run$ = new BehaviorSubject<void>(undefined);

  loading = false;
  errorMsg: string | null = null;

  readonly compare$ = this.run$.pipe(
    tap(() => {
      this.loading = true;
      this.errorMsg = null;
    }),
    switchMap(() => {
      const ids = this.selected.map(d => d.id);
      const from = this.fromCtrl.value ?? undefined;
      const to = this.toCtrl.value ?? undefined;
      const mode = this.modeCtrl.value ?? undefined;

      if (ids.length < 2) {
        this.loading = false;
        return of<CompareResponse | null>(null);
      }

      return this.compareApi.compare(ids, from, to, mode).pipe(
        tap(() => (this.loading = false)),
        catchError(err => {
          this.loading = false;
          this.errorMsg = err?.error?.message ?? 'Compare failed';
          return of<CompareResponse | null>(null);
        })
      );
    })
  );

  constructor(
    private readonly compareApi: CompareApiService,
    private readonly scoringModesApi: ScoringModesApiService
  ) {
    this.scoringModesApi.list().subscribe({
      next: m => (this.modes = m),
      error: () => (this.modes = [
        { id: 'GREATEST_BALANCED', label: 'Greatest Balanced', description: 'Default balanced mode.', weights: [] }
      ])
    });
  }

  onSelected(list: DriverSuggestion[]): void {
    this.selected = list;
  }

  runCompare(): void {
    this.run$.next();
  }
}
