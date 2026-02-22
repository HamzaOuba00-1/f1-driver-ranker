package com.acme.f1ranker.domain.model;

public record NormalizedFeatures(
        String driverId,
        double winRateNorm,
        double podiumRateNorm,
        double dnfRateNorm
) {}