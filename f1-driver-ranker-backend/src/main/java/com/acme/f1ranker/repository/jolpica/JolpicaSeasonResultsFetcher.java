package com.acme.f1ranker.repository.jolpica;

import com.acme.f1ranker.repository.ResultsRepository.RaceResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class JolpicaSeasonResultsFetcher {

    private static final int LIMIT = 100;
    private final RestClient restClient;

    public JolpicaSeasonResultsFetcher(RestClient jolpicaRestClient) {
        this.restClient = jolpicaRestClient;
    }

    @Cacheable(cacheNames = "jolpica.results.byDriverSeason", key = "#driverId + ':' + #season")
    public List<RaceResult> fetchSeason(String driverId, int season) {
        List<RaceResult> seasonResults = new ArrayList<>();

        int offset = 0;
        int total = Integer.MAX_VALUE;

        while (offset < total) {
            final int currentOffset = offset;

            JolpicaDtos.RaceResultsResponse resp = getWithRetry(season, driverId, currentOffset);

            if (resp == null || resp.mrData() == null || resp.mrData().raceTable() == null) break;

            List<JolpicaDtos.Race> races = resp.mrData().raceTable().races();
            if (races == null || races.isEmpty()) break;

            for (JolpicaDtos.Race race : races) {
                Integer round = parseIntOrNull(race.round());
                Integer finishPos = extractFinishPosition(race);
                if (round != null) seasonResults.add(new RaceResult(season, round, finishPos));
            }

            total = parseIntOrDefault(resp.mrData().total(), seasonResults.size());
            offset += LIMIT;
        }

        return seasonResults;
    }

    private static Integer extractFinishPosition(JolpicaDtos.Race race) {
        if (race.results() == null || race.results().isEmpty())
            return null;
        JolpicaDtos.Result r = race.results().getFirst();
        return parseIntOrNull(r.position());
    }

    private static int parseIntOrDefault(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignored) {
            return def;
        }
    }

    private static Integer parseIntOrNull(String s) {
        try {
            if (s == null)
                return null;
            String t = s.trim();
            if (t.isEmpty())
                return null;
            return Integer.parseInt(t);
        } catch (Exception ignored) {
            return null;
        }
    }

    private JolpicaDtos.RaceResultsResponse getWithRetry(int season, String driverId, int offset) {
        int attempts = 0;
        long sleepMs = 250;

        while (true) {
            attempts++;
            try {
                return restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/" + season + "/drivers/" + driverId + "/results.json")
                                .queryParam("limit", LIMIT)
                                .queryParam("offset", offset)
                                .build())
                        .retrieve()
                        .body(JolpicaDtos.RaceResultsResponse.class);

            } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
                if (attempts >= 4)
                    throw e;
                sleep(sleepMs);
                sleepMs *= 2;
            }
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}