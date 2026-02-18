import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, ChangeDetectionStrategy, OnDestroy } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, catchError, of, startWith, Subject, takeUntil, map } from 'rxjs';
import { DriversApiService } from '../../../../core/api/drivers-api.service';
import { DriverSuggestion } from '../../../../core/models/driver.models';

@Component({
  selector: 'app-driver-multi-picker',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './driver-multi-picker.component.html',
  styleUrl: './driver-multi-picker.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DriverMultiPickerComponent implements OnDestroy {
  @Output() selectedChange = new EventEmitter<DriverSuggestion[]>();

  readonly queryCtrl = new FormControl<string>('', { nonNullable: true });

  selected: DriverSuggestion[] = [];
  suggestions: DriverSuggestion[] = [];
  highlightedIndex = -1;

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly driversApi: DriversApiService) {
    this.queryCtrl.valueChanges.pipe(
      startWith(this.queryCtrl.value),
      map(v => (v ?? '').trim()),
      debounceTime(200),
      distinctUntilChanged(),
      switchMap(q => {
        if (q.length < 2) return of([] as DriverSuggestion[]);
        return this.driversApi.search(q).pipe(catchError(() => of([] as DriverSuggestion[])));
      }),
      takeUntil(this.destroy$)
    ).subscribe(list => {
      this.suggestions = list;
      this.highlightedIndex = list.length ? 0 : -1;
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  add(driver: DriverSuggestion): void {
    if (this.selected.some(d => d.id === driver.id)) return;

    this.selected = [...this.selected, driver];
    this.selectedChange.emit(this.selected);

    this.queryCtrl.setValue('');
    this.suggestions = [];
    this.highlightedIndex = -1;
  }

  remove(id: string): void {
    this.selected = this.selected.filter(d => d.id !== id);
    this.selectedChange.emit(this.selected);
  }

  onKeyDown(event: KeyboardEvent): void {
    const list = this.suggestions;
    if (!list.length) return;

    if (event.key === 'ArrowDown') {
      event.preventDefault();
      this.highlightedIndex = Math.min(this.highlightedIndex + 1, list.length - 1);
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      this.highlightedIndex = Math.max(this.highlightedIndex - 1, 0);
    } else if (event.key === 'Enter') {
      if (this.highlightedIndex >= 0 && this.highlightedIndex < list.length) {
        event.preventDefault();
        this.add(list[this.highlightedIndex]);
      }
    } else if (event.key === 'Escape') {
      this.highlightedIndex = -1;
    }
  }

  pickAt(index: number): void {
    if (index < 0 || index >= this.suggestions.length) return;
    this.add(this.suggestions[index]);
  }

  trackById(_: number, d: DriverSuggestion): string {
    return d.id;
  }
}
