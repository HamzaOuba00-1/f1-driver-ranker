package com.acme.f1ranker.controller.dto;

import java.util.List;

public record ModeDto(
        String id,
        String label,
        String description,
        List<MetricWeightDto> weights
) {
    public record MetricWeightDto(
            String metricId,
            double weight
    ) {}
}