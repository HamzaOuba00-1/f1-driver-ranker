package com.acme.f1ranker.controller;

import com.acme.f1ranker.controller.dto.CompareResponseDto;
import com.acme.f1ranker.domain.engine.RankingEngine;
import com.acme.f1ranker.domain.model.MetricContribution;
import com.acme.f1ranker.service.CompareService;
import com.acme.f1ranker.service.scoring.ScoringMode;
import com.acme.f1ranker.service.scoring.StrategyRegistry;
import jakarta.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/compare")
public class CompareController {

    private final CompareService compareService;
    private final StrategyRegistry strategyRegistry;

    private final RankingEngine rankingEngine = new RankingEngine();

    public CompareController(CompareService compareService, StrategyRegistry strategyRegistry) {
        this.compareService = compareService;
        this.strategyRegistry = strategyRegistry;
    }

    @GetMapping
    public CompareResponseDto compare(
            @RequestParam("drivers")
            @Size(min = 3, max = 200, message = "drivers param too long")
            String driversCsv,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "to", required = false) Integer to,
            @RequestParam(value = "mode", required = false) ScoringMode mode
    ) {
        List<String> driverIds = parseDrivers(driversCsv);

        var strategy = strategyRegistry.getOrDefault(mode);

        // We still need from/to used by CompareService; re-use existing result for period.
        var baseline = compareService.compare(driverIds, from, to);
        var features = baseline.ranking().stream().map(CompareService.RankedFeatures::features).toList();

        var ranked = rankingEngine.rank(features, strategy);

        List<CompareResponseDto.RankingEntryDto> rankingDto = ranked.ranking().stream()
                .map(re -> {
                    var e = re.entry();
                    var f = e.features();
                    var n = e.normalized();

                    List<CompareResponseDto.MetricContributionDto> contributions = e.contributions().stream()
                            .map(CompareController::toDto)
                            .toList();

                    return new CompareResponseDto.RankingEntryDto(
                            re.rank(),
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
                            n.dnfRateNorm(),
                            e.finalScore(),
                            contributions
                    );
                })
                .toList();

        return new CompareResponseDto(strategy.mode().name(), baseline.fromSeason(), baseline.toSeason(), rankingDto);
    }

    private static CompareResponseDto.MetricContributionDto toDto(MetricContribution c) {
        return new CompareResponseDto.MetricContributionDto(
                c.metricId().name(),
                c.rawValue(),
                c.normalizedValue(),
                c.weight(),
                c.contribution()
        );
    }

    private static List<String> parseDrivers(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }
}