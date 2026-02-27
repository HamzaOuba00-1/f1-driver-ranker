package com.acme.f1ranker.service.scoring;

import com.acme.f1ranker.domain.model.DriverFeatures;
import com.acme.f1ranker.domain.model.NormalizedFeatures;

public interface ScoringStrategy {

    ScoringMode mode();

    ScoreOutput score(DriverFeatures features, NormalizedFeatures normalized);

    record ScoreOutput(
            double finalScore,
            java.util.List<com.acme.f1ranker.domain.model.MetricContribution> contributions
    ) {}
}