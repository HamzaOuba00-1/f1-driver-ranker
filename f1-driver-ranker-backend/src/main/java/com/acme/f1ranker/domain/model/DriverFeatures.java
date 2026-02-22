package com.acme.f1ranker.domain.model;

public record DriverFeatures(
        String driverId,
        int races,
        int wins,
        int podiums,
        int dnfs,
        double winRate,
        double podiumRate,
        double dnfRate
) {}