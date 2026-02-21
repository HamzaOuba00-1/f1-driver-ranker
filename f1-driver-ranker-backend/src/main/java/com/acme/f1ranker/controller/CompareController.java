package com.acme.f1ranker.controller;

import com.acme.f1ranker.controller.dto.CompareResponseDto;
import com.acme.f1ranker.service.CompareService;
import jakarta.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/compare")
public class CompareController {

    private final CompareService compareService;

    public CompareController(CompareService compareService) {
        this.compareService = compareService;
    }

    @GetMapping
    public CompareResponseDto compare(
            @RequestParam("drivers")
            @Size(min = 3, max = 200, message = "drivers param too long")
            String driversCsv,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "to", required = false) Integer to
    ) {
        List<String> driverIds = parseDrivers(driversCsv);

        var result = compareService.compare(driverIds, from, to);

        List<CompareResponseDto.RankingEntryDto> ranking = result.ranking().stream()
                .map(r -> new CompareResponseDto.RankingEntryDto(
                        r.rank(),
                        r.stats().driverId(),
                        r.stats().races(),
                        r.stats().wins(),
                        r.stats().podiums()
                ))
                .toList();

        return new CompareResponseDto(result.fromSeason(), result.toSeason(), ranking);
    }

    private static List<String> parseDrivers(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}