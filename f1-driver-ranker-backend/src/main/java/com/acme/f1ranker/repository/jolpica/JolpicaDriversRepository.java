package com.acme.f1ranker.repository.jolpica;

import com.acme.f1ranker.repository.DriversRepository;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
public class JolpicaDriversRepository implements DriversRepository {

    private final JolpicaClient jolpicaClient;

    public JolpicaDriversRepository(JolpicaClient jolpicaClient) {
        this.jolpicaClient = jolpicaClient;
    }

    @Override
    public List<DriverSuggestion> searchByQuery(String query, int limit) {
        String q = query == null ? "" : query.trim();
        if (q.length() < 2) return List.of();

        var byGiven = jolpicaClient.searchDriversByGivenName(q);
        var byFamily = jolpicaClient.searchDriversByFamilyName(q);

        return java.util.stream.Stream.concat(byGiven.stream(), byFamily.stream())
                .map(JolpicaMapper::toSuggestion)
                .collect(java.util.stream.Collectors.toMap(
                        DriverSuggestion::id,
                        s -> s,
                        (a, b) -> a
                ))
                .values().stream()
                .limit(limit)
                .toList();
    }


    private static int score(DriverSuggestion d, String q) {
        String id = normalize(d.id());
        String name = normalize(d.fullName());

        if (id.equals(q) || name.equals(q))
            return 100;
        if (id.startsWith(q) || name.startsWith(q))
            return 80;
        if (id.contains(q) || name.contains(q))
            return 60;
        return 0;
    }

    private static String normalize(String input) {
        String s = input == null ? "" : input.trim().toLowerCase(Locale.ROOT);
        s = Normalizer.normalize(s, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
        return s;
    }
}
