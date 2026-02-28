package com.acme.f1ranker.controller.dto;

import java.util.List;

public record CompareResponseDto(
        String mode,
        int fromSeason,
        int toSeason,
        List<RankingEntryDto> ranking
) {
    public record RankingEntryDto(
            int rank,
            String driverId,

            // raw
            int races,
            int wins,
            int podiums,
            int dnfs,

            // rates
            double winRate,
            double podiumRate,
            double dnfRate,

            // normalized
            double winRateNorm,
            double podiumRateNorm,
            double dnfRateNorm,

            // scoring
            double finalScore,
            List<MetricContributionDto> contributions
    ) {}

    public record MetricContributionDto(
            String metricId,
            double rawValue,
            double normalizedValue,
            double weight,
            double contribution
    ) {}
}