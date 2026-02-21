package com.acme.f1ranker.repository.jolpica;

import com.acme.f1ranker.repository.ResultsRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class JolpicaResultsRepository implements ResultsRepository {

    private final JolpicaSeasonResultsFetcher seasonFetcher;

    public JolpicaResultsRepository(JolpicaSeasonResultsFetcher seasonFetcher) {
        this.seasonFetcher = seasonFetcher;
    }

    @Override
    public List<RaceResult> fetchRaceResults(String driverId, int fromSeason, int toSeason) {
        List<RaceResult> out = new ArrayList<>();
        for (int season = fromSeason; season <= toSeason; season++) {
            out.addAll(seasonFetcher.fetchSeason(driverId, season));
        }
        return out;
    }
}