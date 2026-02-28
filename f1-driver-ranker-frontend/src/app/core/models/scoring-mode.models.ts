export interface ScoringModeDto {
  id: string;
  label: string;
  description: string;
  weights: { metricId: string; weight: number }[];
}
