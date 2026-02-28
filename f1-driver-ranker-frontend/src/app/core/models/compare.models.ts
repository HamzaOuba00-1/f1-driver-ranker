export interface CompareResponse {
  mode: string;
  fromSeason: number;
  toSeason: number;
  ranking: RankingEntry[];
}

export interface RankingEntry {
  rank: number;
  driverId: string;

  races: number;
  wins: number;
  podiums: number;
  dnfs: number;

  winRate: number;
  podiumRate: number;
  dnfRate: number;

  winRateNorm: number;
  podiumRateNorm: number;
  dnfRateNorm: number;

  finalScore: number;
  contributions: MetricContribution[];
}

export interface MetricContribution {
  metricId: string;
  rawValue: number;
  normalizedValue: number;
  weight: number;
  contribution: number;
}
