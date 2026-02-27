package com.acme.f1ranker.domain.model;

public record MetricContribution(
        MetricId metricId,
        double rawValue,
        double normalizedValue,
        double weight,
        double contribution
) {}