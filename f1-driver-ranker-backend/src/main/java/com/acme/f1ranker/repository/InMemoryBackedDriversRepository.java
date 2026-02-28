package com.acme.f1ranker.repository;

import com.acme.f1ranker.repository.jolpica.JolpicaClient;
import com.acme.f1ranker.repository.jolpica.JolpicaMapper;
import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class InMemoryBackedDriversRepository implements DriversRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryBackedDriversRepository.class);

    private final JolpicaClient jolpicaClient;

    /**
     * Snapshot immuable des drivers servis aux requêtes.
     * Remplacé atomiquement lors des refresh.
     */
    private final AtomicReference<List<DriverSuggestion>> snapshot = new AtomicReference<>(FallbackDrivers.DEFAULT);

    private volatile Instant lastRefresh = null;

    public InMemoryBackedDriversRepository(JolpicaClient jolpicaClient) {
        this.jolpicaClient = jolpicaClient;
    }

    @Override
    public List<DriverSuggestion> searchByQuery(String query, int limit) {
        String q = normalize(query);
        if (q.isEmpty() || q.length() < 2)
            return List.of();

        List<DriverSuggestion> current = snapshot.get();

        return current.stream()
                .filter(d -> normalize(d.id()).contains(q) || normalize(d.fullName()).contains(q))
                .sorted(Comparator.comparingInt((DriverSuggestion d) -> score(d, q)).reversed())
                .limit(limit)
                .toList();
    }

    /**
     * Warm-up au démarrage : on tente de charger Jolpica une fois,
     * mais on ne bloque pas le service si ça échoue.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmUp() {
        refreshNow("startup");
    }

    /**
     * Refresh périodique (par défaut toutes les 12h).
     * Ajuste via application.yml si besoin.
     */
    @Scheduled(fixedDelayString = "${cache.drivers.ttl:PT12H}")
    public void scheduledRefresh() {
        refreshNow("scheduled");
    }

    public void refreshNow(String reason) {
        Instant start = Instant.now();
        try {
            var drivers = jolpicaClient.fetchAllDrivers();
            if (drivers == null || drivers.isEmpty()) {
                log.warn("Drivers refresh ({}) returned empty list, keeping previous snapshot.", reason);
                return;
            }

            List<DriverSuggestion> next = drivers.stream()
                    .map(JolpicaMapper::toSuggestion)
                    .toList();

            snapshot.set(next);
            lastRefresh = Instant.now();

            log.info("Drivers refresh ({}) OK: {} drivers in {} ms",
                    reason,
                    next.size(),
                    Duration.between(start, lastRefresh).toMillis());
        } catch (Exception e) {
            log.warn("Drivers refresh ({}) failed, keeping previous snapshot. Cause: {}", reason, e.toString());
        }
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

    /**
     * Fallback minimal: sert si Jolpica est lent/down au démarrage.
     * Tu peux enrichir autant que tu veux.
     */
    static final class FallbackDrivers {
        private FallbackDrivers() {
        }

        static final List<DriverSuggestion> DEFAULT = List.of(
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
                new DriverSuggestion("russell", "George Russell", "GBR", 2019, null));
    }
}