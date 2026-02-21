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
            int races,
            int wins,
            int podiums
    ) {}
}