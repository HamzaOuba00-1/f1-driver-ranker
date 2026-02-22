package com.acme.f1ranker.controller;

import com.acme.f1ranker.controller.dto.CompareResponseDto;
import com.acme.f1ranker.domain.engine.Normalizer;
import com.acme.f1ranker.domain.model.DriverFeatures;
import com.acme.f1ranker.domain.model.NormalizedFeatures;
import com.acme.f1ranker.service.CompareService;
import jakarta.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/compare")
public class CompareController {

    private final CompareService compareService;
    private final Normalizer normalizer = new Normalizer();

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

        // collect features list (already sorted by CompareService)
        List<DriverFeatures> featuresList = result.ranking().stream()
                .map(CompareService.RankedFeatures::features)
                .toList();

        List<NormalizedFeatures> normalized = normalizer.normalize(featuresList);
        Map<String, NormalizedFeatures> normById = normalized.stream()
                .collect(Collectors.toMap(NormalizedFeatures::driverId, Function.identity()));

        List<CompareResponseDto.RankingEntryDto> ranking = result.ranking().stream()
                .map(r -> {
                    DriverFeatures f = r.features();
                    NormalizedFeatures n = normById.get(f.driverId());

                    return new CompareResponseDto.RankingEntryDto(
                            r.rank(),
                            f.driverId(),
                            f.races(),
                            f.wins(),
                            f.podiums(),
                            f.dnfs(),
                            f.winRate(),
                            f.podiumRate(),
                            f.dnfRate(),
                            n.winRateNorm(),
                            n.podiumRateNorm(),
                            n.dnfRateNorm()
                    );
                })
                .toList();

        return new CompareResponseDto(result.fromSeason(), result.toSeason(), ranking);
    }

    private static List<String> parseDrivers(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }
}