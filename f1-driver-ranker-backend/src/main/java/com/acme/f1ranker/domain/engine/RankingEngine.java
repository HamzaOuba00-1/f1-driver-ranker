package com.acme.f1ranker.domain.engine;

import com.acme.f1ranker.domain.model.DriverFeatures;
import com.acme.f1ranker.domain.model.NormalizedFeatures;
import com.acme.f1ranker.service.scoring.ScoringStrategy;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RankingEngine {

    private final Normalizer normalizer = new Normalizer();

    public RankingResult rank(List<DriverFeatures> features, ScoringStrategy strategy) {
        List<NormalizedFeatures> normalized = normalizer.normalize(features);

        Map<String, NormalizedFeatures> normById = normalized.stream()
                .collect(Collectors.toMap(NormalizedFeatures::driverId, Function.identity()));

        List<RankingEntry> entries = features.stream()
                .map(f -> {
                    NormalizedFeatures n = normById.get(f.driverId());
                    var scored = strategy.score(f, n);

                    return new RankingEntry(
                            f,
                            n,
                            scored.finalScore(),
                            scored.contributions()
                    );
                })
                .sorted(Comparator.comparingDouble(RankingEntry::finalScore).reversed()
                        .thenComparing(e -> e.features().driverId()))
                .toList();

        List<RankedEntry> ranked = java.util.stream.IntStream.range(0, entries.size())
                .mapToObj(i -> new RankedEntry(i + 1, entries.get(i)))
                .toList();

        return new RankingResult(ranked);
    }

    public record RankingResult(List<RankedEntry> ranking) {}

    public record RankedEntry(int rank, RankingEntry entry) {}

    public record RankingEntry(
            DriverFeatures features,
            NormalizedFeatures normalized,
            double finalScore,
            java.util.List<com.acme.f1ranker.domain.model.MetricContribution> contributions
    ) {}
}