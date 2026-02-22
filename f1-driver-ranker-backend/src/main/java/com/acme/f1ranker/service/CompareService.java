package com.acme.f1ranker.service;

import com.acme.f1ranker.domain.engine.AggregateBuilder;
import com.acme.f1ranker.domain.engine.FeatureComputer;
import com.acme.f1ranker.domain.model.DriverAggregate;
import com.acme.f1ranker.domain.model.DriverFeatures;
import com.acme.f1ranker.repository.ResultsRepository;


import java.time.Year;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CompareService {

    private static final int MIN_SEASON = 1950;

    private final ResultsRepository resultsRepository;
    private final AggregateBuilder aggregateBuilder = new AggregateBuilder();
    private final FeatureComputer featureComputer = new FeatureComputer();

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

        List<DriverAggregate> aggregates = driverIds.stream()
                .distinct()
                .map(id -> {
                    var results = resultsRepository.fetchRaceResults(id, fromSeason, toSeason);
                    return aggregateBuilder.build(id, results);
                })
                .toList();

        List<DriverFeatures> features = aggregates.stream()
                .map(featureComputer::compute)
                .toList();

        List<DriverFeatures> sorted = features.stream()
                .sorted(Comparator
                        .comparingInt(DriverFeatures::wins).reversed()
                        .thenComparingInt(DriverFeatures::podiums).reversed()
                        .thenComparingInt(DriverFeatures::races).reversed()
                        .thenComparing(DriverFeatures::driverId))
                .toList();

        List<RankedFeatures> ranked = rank(sorted);
        return new CompareResult(fromSeason, toSeason, ranked);
    }

    private DriverStats computeStats(String driverId, int fromSeason, int toSeason) {
        var results = resultsRepository.fetchRaceResults(driverId, fromSeason, toSeason);

        int races = results.size();
        int wins = 0;
        int podiums = 0;

        for (ResultsRepository.RaceResult rr : results) {
            Integer pos = rr.finishPosition();
            if (pos == null)
                continue;
            if (pos == 1)
                wins++;
            if (pos <= 3)
                podiums++;
        }

        return new DriverStats(driverId, races, wins, podiums);
    }

    // Java trick: keep rank increasing without mutable captured vars
    private static int rankCounter = 1;

    private static int rankAndIncrement(int ignored) {
        return rankCounter++;
    }

    private static int rankAndIncrementReset() {
        return rankCounter = 1;
    }

    // Replace rank(...) call above with rankReset(stats) if you prefer
    // deterministic rank per request
    // (recommended). For simplicity + correctness, let's do it:
    // return new CompareResult(fromSeason, toSeason, rankReset(stats));

    public record DriverStats(
            String driverId,
            int races,
            int wins,
            int podiums) {
    }

    public record RankedDriverStats(
            int rank,
            DriverStats stats) {
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public record RankedFeatures(int rank, com.acme.f1ranker.domain.model.DriverFeatures features) {
    }

    public record CompareResult(int fromSeason, int toSeason, java.util.List<RankedFeatures> ranking) {
    }

    private static java.util.List<RankedFeatures> rank(
            java.util.List<com.acme.f1ranker.domain.model.DriverFeatures> sorted) {
        return java.util.stream.IntStream.range(0, sorted.size())
                .mapToObj(i -> new RankedFeatures(i + 1, sorted.get(i)))
                .toList();
    }

    public record RankedAggregate(int rank, DriverAggregate aggregate) {
    }

}