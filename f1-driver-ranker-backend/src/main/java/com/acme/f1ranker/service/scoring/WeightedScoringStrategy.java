package com.acme.f1ranker.service.scoring;

import com.acme.f1ranker.domain.engine.ExplainabilityEngine;
import com.acme.f1ranker.domain.model.DriverFeatures;
import com.acme.f1ranker.domain.model.MetricContribution;
import com.acme.f1ranker.domain.model.MetricId;
import com.acme.f1ranker.domain.model.NormalizedFeatures;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WeightedScoringStrategy implements ScoringStrategy {

    private final ExplainabilityEngine explainabilityEngine = new ExplainabilityEngine();

    @Override
    public ScoringMode mode() {
        return ScoringMode.GREATEST_BALANCED;
    }

    @Override
    public ScoreOutput score(DriverFeatures f, NormalizedFeatures n) {
        Map<MetricId, Double> weights = defaultWeights();

        Map<MetricId, Double> raw = new EnumMap<>(MetricId.class);
        raw.put(MetricId.WIN_RATE, f.winRate());
        raw.put(MetricId.PODIUM_RATE, f.podiumRate());
        raw.put(MetricId.DNF_RATE, f.dnfRate());

        Map<MetricId, Double> norm = new EnumMap<>(MetricId.class);
        norm.put(MetricId.WIN_RATE, n.winRateNorm());
        norm.put(MetricId.PODIUM_RATE, n.podiumRateNorm());
        norm.put(MetricId.DNF_RATE, n.dnfRateNorm()); // already inverted => higher is better

        List<MetricContribution> contributions = explainabilityEngine.build(weights, raw, norm);

        double finalScore = contributions.stream()
                .mapToDouble(MetricContribution::contribution)
                .sum();

        return new ScoreOutput(finalScore, contributions);
    }

    private static Map<MetricId, Double> defaultWeights() {
        // sum to 1.0
        Map<MetricId, Double> w = new EnumMap<>(MetricId.class);
        w.put(MetricId.WIN_RATE, 0.55);
        w.put(MetricId.PODIUM_RATE, 0.30);
        w.put(MetricId.DNF_RATE, 0.15);
        return w;
    }
}