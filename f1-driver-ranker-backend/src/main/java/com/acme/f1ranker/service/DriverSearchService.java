package com.acme.f1ranker.service;

import com.acme.f1ranker.repository.DriversRepository;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class DriverSearchService {

    private static final int DEFAULT_LIMIT = 10;

    // Allow letters, spaces, hyphen, apostrophe, dot (basic safe search query)
    private static final Pattern SAFE_QUERY = Pattern.compile("^[\\p{L}0-9 .\\-']{1,50}$");

    private final DriversRepository driversRepository;

    public DriverSearchService(DriversRepository driversRepository) {
        this.driversRepository = driversRepository;
    }

    public List<DriversRepository.DriverSuggestion> search(String rawQuery) {
        String query = sanitize(rawQuery);

        if (query.length() < 2) {
            return List.of();
        }

        return driversRepository.searchByQuery(query, DEFAULT_LIMIT);
    }

    private String sanitize(String raw) {
        String q = raw == null ? "" : raw.trim();

        if (q.isEmpty()) return "";

        if (!SAFE_QUERY.matcher(q).matches()) {
            throw new IllegalArgumentException("Invalid query");
        }

        return q;
    }
}
