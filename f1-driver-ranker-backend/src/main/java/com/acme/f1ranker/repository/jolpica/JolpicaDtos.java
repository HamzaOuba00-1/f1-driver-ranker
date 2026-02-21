package com.acme.f1ranker.repository.jolpica;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class JolpicaDtos {

        private JolpicaDtos() {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record DriversResponse(
                        @JsonProperty("MRData") MRData mrData) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record MRData(
                        String total,
                        String limit,
                        String offset,
                        @JsonProperty("DriverTable") DriverTable driverTable) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record DriverTable(
                        @JsonProperty("Drivers") List<Driver> drivers) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Driver(
                        String driverId,
                        String givenName,
                        String familyName,
                        String nationality,
                        String dateOfBirth,
                        String url) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record RaceResultsResponse(
                        @JsonProperty("MRData") MRDataRace mrData) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record MRDataRace(
                        String total,
                        String limit,
                        String offset,
                        @JsonProperty("RaceTable") RaceTable raceTable) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record RaceTable(
                        @JsonProperty("Races") List<Race> races) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Race(
                        String season,
                        String round,
                        String raceName,
                        @JsonProperty("Results") List<Result> results) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Result(
                        String position,
                        String positionText,
                        String status) {
        }
}
