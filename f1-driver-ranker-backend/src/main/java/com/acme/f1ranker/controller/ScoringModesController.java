package com.acme.f1ranker.controller;

import com.acme.f1ranker.controller.dto.ModeDto;
import com.acme.f1ranker.domain.model.MetricId;
import com.acme.f1ranker.service.scoring.ScoringMode;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scoring-modes")
public class ScoringModesController {

    @GetMapping
    public List<ModeDto> listModes() {
        return List.of(
                new ModeDto(
                        ScoringMode.GREATEST_BALANCED.name(),
                        "Greatest Balanced",
                        "Balanced mode combining win rate, podium rate and reliability.",
                        List.of(
                                new ModeDto.MetricWeightDto(MetricId.WIN_RATE.name(), 0.55),
                                new ModeDto.MetricWeightDto(MetricId.PODIUM_RATE.name(), 0.30),
                                new ModeDto.MetricWeightDto(MetricId.DNF_RATE.name(), 0.15)
                        )
                ),
                new ModeDto(
                        ScoringMode.RACE_KING.name(),
                        "Race King",
                        "Prioritizes wins and podiums; reliability matters less.",
                        List.of(
                                new ModeDto.MetricWeightDto(MetricId.WIN_RATE.name(), 0.70),
                                new ModeDto.MetricWeightDto(MetricId.PODIUM_RATE.name(), 0.25),
                                new ModeDto.MetricWeightDto(MetricId.DNF_RATE.name(), 0.05)
                        )
                ),
                new ModeDto(
                        ScoringMode.CONSISTENCY.name(),
                        "Consistency",
                        "Prioritizes reliability and steady results.",
                        List.of(
                                new ModeDto.MetricWeightDto(MetricId.WIN_RATE.name(), 0.25),
                                new ModeDto.MetricWeightDto(MetricId.PODIUM_RATE.name(), 0.35),
                                new ModeDto.MetricWeightDto(MetricId.DNF_RATE.name(), 0.40)
                        )
                ),
                new ModeDto(
                        ScoringMode.PARETO_NO_WEIGHTS.name(),
                        "Pareto No-Weights",
                        "Experimental: will rank by Pareto fronts (no weights).",
                        List.of()
                )
        );
    }
}