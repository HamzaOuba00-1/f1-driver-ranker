import { CommonModule } from '@angular/common';
import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { RankingEntry } from '../../../../core/models/compare.models';
import { DriverSuggestion } from '../../../../core/models/driver.models';

@Component({
  selector: 'app-ranking-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ranking-table.component.html',
  styleUrl: './ranking-table.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RankingTableComponent {
  @Input({ required: true }) ranking: RankingEntry[] = [];
  @Input() selectedDrivers: DriverSuggestion[] = [];

  nameOf(driverId: string): string {
    const found = this.selectedDrivers.find(d => d.id === driverId);
    return found?.fullName ?? driverId;
  }

  trackByDriverId(_: number, r: RankingEntry): string {
    return r.driverId;
  }
}
