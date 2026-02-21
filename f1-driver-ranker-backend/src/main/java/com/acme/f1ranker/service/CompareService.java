package com.acme.f1ranker.service;

import com.acme.f1ranker.repository.ResultsRepository;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CompareService {

    private static final int MIN_SEASON = 1950;

    private final ResultsRepository resultsRepository;

    public CompareService(ResultsRepository resultsRepository) {
        this.resultsRepository = resultsRepository;
    }

    public CompareResult compare(List<String> driverIds, Integer from, Integer to) {
        if (driverIds == null || driverIds.size() < 2) {
            throw new IllegalArgumentException("At least 2 drivers are required");
        }

        int currentYear = Year.now().getValue();
        int fromSeason = clamp(from == null ? MIN_SEASON : from, MIN_SEASON, currentYear);
        int toSeason = clamp(to == null ? currentYear : to, MIN_SEASON, currentYear);

        if (fromSeason > toSeason) {
            throw new IllegalArgumentException("from must be <= to");
        }

        List<DriverStats> stats = driverIds.stream()
                .distinct()
                .map(id -> computeStats(id, fromSeason, toSeason))
                .sorted(Comparator
                        .comparingInt(DriverStats::wins).reversed()
                        .thenComparingInt(DriverStats::podiums).reversed()
                        .thenComparingInt(DriverStats::races).reversed()
                        .thenComparing(DriverStats::driverId))
                .toList();

        return new CompareResult(fromSeason, toSeason, rankReset(stats));
    }

    private DriverStats computeStats(String driverId, int fromSeason, int toSeason) {
        var results = resultsRepository.fetchRaceResults(driverId, fromSeason, toSeason);

        int races = results.size();
        int wins = 0;
        int podiums = 0;

        for (ResultsRepository.RaceResult rr : results) {
            Integer pos = rr.finishPosition();
            if (pos == null) continue;
            if (pos == 1) wins++;
            if (pos <= 3) podiums++;
        }

        return new DriverStats(driverId, races, wins, podiums);
    }

    private static List<RankedDriverStats> rank(List<DriverStats> sorted) {
        int rank = 1;
        return sorted.stream()
                .map(s -> new RankedDriverStats(rankAndIncrement(rank), s))
                .toList();
    }

    // Java trick: keep rank increasing without mutable captured vars
    private static int rankCounter = 1;
    private static int rankAndIncrement(int ignored) {
        return rankCounter++;
    }
    private static int rankAndIncrementReset() { return rankCounter = 1; }

    private static List<RankedDriverStats> rankReset(List<DriverStats> sorted) {
        rankAndIncrementReset();
        return sorted.stream()
                .map(s -> new RankedDriverStats(rankAndIncrement(0), s))
                .toList();
    }

    // Replace rank(...) call above with rankReset(stats) if you prefer deterministic rank per request
    // (recommended). For simplicity + correctness, let's do it:
    // return new CompareResult(fromSeason, toSeason, rankReset(stats));

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public record CompareResult(
            int fromSeason,
            int toSeason,
            List<RankedDriverStats> ranking
    ) {}

    public record DriverStats(
            String driverId,
            int races,
            int wins,
            int podiums
    ) {}

    public record RankedDriverStats(
            int rank,
            DriverStats stats
    ) {}
}