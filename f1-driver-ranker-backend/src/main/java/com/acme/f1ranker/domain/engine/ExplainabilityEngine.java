package com.acme.f1ranker.domain.engine;

import com.acme.f1ranker.domain.model.MetricContribution;
import com.acme.f1ranker.domain.model.MetricId;
import java.util.List;
import java.util.Map;

public class ExplainabilityEngine {

    public List<MetricContribution> build(
            Map<MetricId, Double> weights,
            Map<MetricId, Double> raw,
            Map<MetricId, Double> normalized
    ) {
        return weights.entrySet().stream()
                .map(e -> {
                    MetricId id = e.getKey();
                    double w = e.getValue();
                    double rawVal = raw.getOrDefault(id, 0.0);
                    double normVal = normalized.getOrDefault(id, 0.0);
                    double c = w * normVal;

                    return new MetricContribution(id, rawVal, normVal, w, c);
                })
                .toList();
    }
}