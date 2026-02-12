package com.acme.f1ranker.controller.dto;

public record DriverSuggestionDto(
        String id,
        String fullName,
        String nationality,
        Integer firstSeason,
        Integer lastSeason
) {
}
