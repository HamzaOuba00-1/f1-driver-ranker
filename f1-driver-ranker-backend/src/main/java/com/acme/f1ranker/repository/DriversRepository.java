package com.acme.f1ranker.repository;

import java.util.List;

public interface DriversRepository {

    List<DriverSuggestion> searchByQuery(String query, int limit);

    record DriverSuggestion(
            String id,
            String fullName,
            String nationality,
            Integer firstSeason,
            Integer lastSeason
    ) {}
}
