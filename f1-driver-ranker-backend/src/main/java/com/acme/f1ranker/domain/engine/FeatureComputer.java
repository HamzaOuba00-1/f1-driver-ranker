package com.acme.f1ranker.domain.engine;

import com.acme.f1ranker.domain.model.DriverAggregate;
import com.acme.f1ranker.domain.model.DriverFeatures;

public class FeatureComputer {

    public DriverFeatures compute(DriverAggregate agg) {
        int races = agg.races();
        double winRate = rate(agg.wins(), races);
        double podiumRate = rate(agg.podiums(), races);
        double dnfRate = rate(agg.dnfs(), races);

        return new DriverFeatures(
                agg.driverId(),
                agg.races(),
                agg.wins(),
                agg.podiums(),
                agg.dnfs(),
                winRate,
                podiumRate,
                dnfRate
        );
    }

    private static double rate(int num, int denom) {
        if (denom <= 0) return 0.0;
        return (double) num / (double) denom;
    }
}