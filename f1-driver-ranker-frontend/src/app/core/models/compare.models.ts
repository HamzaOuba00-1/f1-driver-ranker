export interface CompareResponse {
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
}
