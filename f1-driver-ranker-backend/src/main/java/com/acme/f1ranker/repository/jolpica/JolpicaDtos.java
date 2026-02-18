package com.acme.f1ranker.repository.jolpica;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class JolpicaDtos {

    private JolpicaDtos() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DriversResponse(
            @JsonProperty("MRData") MRData mrData
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MRData(
            String total,
            String limit,
            String offset,
            @JsonProperty("DriverTable") DriverTable driverTable
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DriverTable(
            @JsonProperty("Drivers") List<Driver> drivers
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Driver(
            String driverId,
            String givenName,
            String familyName,
            String nationality,
            String dateOfBirth,
            String url
    ) {}
}
