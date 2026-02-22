package com.acme.f1ranker.domain.engine;

import com.acme.f1ranker.domain.model.DriverFeatures;
import com.acme.f1ranker.domain.model.NormalizedFeatures;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class Normalizer {

    public List<NormalizedFeatures> normalize(List<DriverFeatures> features) {
        Stats winStats = stats(features, DriverFeatures::winRate);
        Stats podiumStats = stats(features, DriverFeatures::podiumRate);
        Stats dnfStats = stats(features, DriverFeatures::dnfRate);

        return features.stream()
                .map(f -> new NormalizedFeatures(
                        f.driverId(),
                        minMax(f.winRate(), winStats),
                        minMax(f.podiumRate(), podiumStats),
                        invert(minMax(f.dnfRate(), dnfStats)) // lower DNF is better
                ))
                .toList();
    }

    private static Stats stats(List<DriverFeatures> list, ToDoubleFunction<DriverFeatures> fn) {
        DoubleSummaryStatistics s = list.stream().mapToDouble(fn).summaryStatistics();
        return new Stats(s.getMin(), s.getMax());
    }

    private static double minMax(double v, Stats s) {
        double min = s.min();
        double max = s.max();
        if (Double.compare(min, max) == 0) return 0.5; // all equal -> neutral
        return clamp01((v - min) / (max - min));
    }

    private static double invert(double norm) {
        return clamp01(1.0 - norm);
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    private record Stats(double min, double max) {}
}