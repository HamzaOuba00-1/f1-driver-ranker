package com.acme.f1ranker.controller.dto;

import java.util.List;

public record CompareResponseDto(
        int fromSeason,
        int toSeason,
        List<RankingEntryDto> ranking
) {
    public record RankingEntryDto(
            int rank,
            String driverId,

            // raw aggregates
            int races,
            int wins,
            int podiums,
            int dnfs,

            // features (0..1)
            double winRate,
            double podiumRate,
            double dnfRate,

            // normalized (0..1)
            double winRateNorm,
            double podiumRateNorm,
            double dnfRateNorm
    ) {}
}