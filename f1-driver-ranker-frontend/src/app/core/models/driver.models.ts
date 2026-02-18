export interface DriverSuggestion {
  id: string;
  fullName: string;
  nationality?: string | null;
  firstSeason?: number | null;
  lastSeason?: number | null;
}
