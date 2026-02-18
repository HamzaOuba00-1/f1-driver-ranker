package com.acme.f1ranker.repository.jolpica;

import com.acme.f1ranker.repository.DriversRepository;

public final class JolpicaMapper {

    private JolpicaMapper() {}

    public static DriversRepository.DriverSuggestion toSuggestion(JolpicaDtos.Driver d) {
        String fullName = (safe(d.givenName()) + " " + safe(d.familyName())).trim();

        return new DriversRepository.DriverSuggestion(
                safe(d.driverId()),
                fullName,
                d.nationality(),
                null,
                null
        );
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
