import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DriverMultiPickerComponent } from './components/driver-multi-picker/driver-multi-picker.component';
import { DriverSuggestion } from '../../core/models/driver.models';

@Component({
  selector: 'app-compare-page',
  standalone: true,
  imports: [CommonModule, DriverMultiPickerComponent],
  template: `
    <section>
      <h2>Compare</h2>
      <p>Phase 1: driver search + multi-select ✅</p>

      <app-driver-multi-picker (selectedChange)="onSelected($event)" />

      <div class="debug" *ngIf="selected.length">
        <h3>Selected</h3>
        <pre>{{ selected | json }}</pre>
      </div>
    </section>
  `,
  styles: [`
    .debug { margin-top: 16px; padding: 12px; border: 1px dashed #ddd; border-radius: 10px; }
    pre { overflow: auto; }
  `]
})
export class ComparePage {
  selected: DriverSuggestion[] = [];

  onSelected(list: DriverSuggestion[]): void {
    this.selected = list;
  }
}
