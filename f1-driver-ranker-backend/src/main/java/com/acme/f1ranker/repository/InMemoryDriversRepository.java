package com.acme.f1ranker.repository;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryDriversRepository implements DriversRepository {

    private static final List<DriverSuggestion> DRIVERS = List.of(
            new DriverSuggestion("hamilton", "Lewis Hamilton", "GBR", 2007, null),
            new DriverSuggestion("verstappen", "Max Verstappen", "NLD", 2015, null),
            new DriverSuggestion("alonso", "Fernando Alonso", "ESP", 2001, null),
            new DriverSuggestion("vettel", "Sebastian Vettel", "DEU", 2007, 2022),
            new DriverSuggestion("schumacher", "Michael Schumacher", "DEU", 1991, 2012),
            new DriverSuggestion("senna", "Ayrton Senna", "BRA", 1984, 1994),
            new DriverSuggestion("prost", "Alain Prost", "FRA", 1980, 1993),
            new DriverSuggestion("lauda", "Niki Lauda", "AUT", 1971, 1985),
            new DriverSuggestion("raikkonen", "Kimi Räikkönen", "FIN", 2001, 2021),
            new DriverSuggestion("leclerc", "Charles Leclerc", "MCO", 2018, null),
            new DriverSuggestion("norris", "Lando Norris", "GBR", 2019, null),
            new DriverSuggestion("sainz", "Carlos Sainz", "ESP", 2015, null),
            new DriverSuggestion("russell", "George Russell", "GBR", 2019, null)
    );

    @Override
    public List<DriverSuggestion> searchByQuery(String query, int limit) {
        String q = normalize(query);

        return DRIVERS.stream()
                .filter(d -> normalize(d.id()).contains(q) || normalize(d.fullName()).contains(q))
                .sorted(Comparator.comparingInt((DriverSuggestion d) -> score(d, q)).reversed())
                .limit(limit)
                .toList();
    }

    private static int score(DriverSuggestion d, String q) {
        String id = normalize(d.id());
        String name = normalize(d.fullName());

        if (id.equals(q) || name.equals(q)) return 100;
        if (id.startsWith(q) || name.startsWith(q)) return 80;
        if (id.contains(q) || name.contains(q)) return 60;
        return 0;
    }

    private static String normalize(String input) {
        String s = input == null ? "" : input.trim().toLowerCase(Locale.ROOT);
        s = Normalizer.normalize(s, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
        return s;
    }
}
