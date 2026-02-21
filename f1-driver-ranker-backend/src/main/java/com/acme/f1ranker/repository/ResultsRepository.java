package com.acme.f1ranker.repository;

import java.util.List;

public interface ResultsRepository {

    List<RaceResult> fetchRaceResults(String driverId, int fromSeason, int toSeason);

    record RaceResult(
            int season,
            int round,
            Integer finishPosition // null if missing/unparseable
    ) {}
}