package com.acme.f1ranker.repository.jolpica;

import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class JolpicaClient {

    private static final int PAGE_SIZE = 100; // Ergast-compatible paging (max 100 recommended)
    private final RestClient restClient;

    public JolpicaClient(RestClient jolpicaRestClient) {
        this.restClient = jolpicaRestClient;
    }

    @Cacheable(cacheNames = "jolpica.drivers.all")
    public List<JolpicaDtos.Driver> fetchAllDrivers() {
        List<JolpicaDtos.Driver> all = new ArrayList<>();

        int offset = 0;
        int total = Integer.MAX_VALUE;

        while (offset < total) {
            final int currentOffset = offset;
            JolpicaDtos.DriversResponse page = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/drivers.json")
                            .queryParam("limit", PAGE_SIZE)
                            .queryParam("offset", currentOffset)
                            .build())
                    .retrieve()
                    .body(JolpicaDtos.DriversResponse.class);

            if (page == null || page.mrData() == null || page.mrData().driverTable() == null) {
                break;
            }

            List<JolpicaDtos.Driver> drivers = page.mrData().driverTable().drivers();
            if (drivers == null || drivers.isEmpty()) {
                break;
            }

            all.addAll(drivers);

            total = parseIntOrDefault(page.mrData().total(), all.size());
            offset += PAGE_SIZE;
        }

        return all;
    }

    private static int parseIntOrDefault(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignored) {
            return def;
        }
    }
}
