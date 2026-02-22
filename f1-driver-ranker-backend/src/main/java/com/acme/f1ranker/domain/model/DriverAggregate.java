package com.acme.f1ranker.domain.model;

public record DriverAggregate(
        String driverId,
        int races,
        int wins,
        int podiums,
        int dnfs
) {}