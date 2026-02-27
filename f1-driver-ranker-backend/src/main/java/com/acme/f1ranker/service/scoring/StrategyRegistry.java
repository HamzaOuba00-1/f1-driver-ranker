package com.acme.f1ranker.service.scoring;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class StrategyRegistry {

    private final Map<ScoringMode, ScoringStrategy> strategies = new EnumMap<>(ScoringMode.class);

    public StrategyRegistry(List<ScoringStrategy> strategies) {
        for (ScoringStrategy s : strategies) {
            this.strategies.put(s.mode(), s);
        }
    }

    public ScoringStrategy getOrDefault(ScoringMode mode) {
        if (mode == null) return require(ScoringMode.GREATEST_BALANCED);
        return strategies.getOrDefault(mode, require(ScoringMode.GREATEST_BALANCED));
    }

    public List<ScoringStrategy> all() {
        return strategies.values().stream().toList();
    }

    private ScoringStrategy require(ScoringMode mode) {
        ScoringStrategy s = strategies.get(mode);
        if (s == null) throw new IllegalStateException("Missing scoring strategy for mode: " + mode);
        return s;
    }
}