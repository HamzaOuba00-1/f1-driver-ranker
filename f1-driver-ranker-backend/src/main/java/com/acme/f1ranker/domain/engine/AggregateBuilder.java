package com.acme.f1ranker.domain.engine;

import com.acme.f1ranker.domain.model.DriverAggregate;
import com.acme.f1ranker.repository.ResultsRepository;
import java.util.List;

public class AggregateBuilder {

    public DriverAggregate build(String driverId, List<ResultsRepository.RaceResult> results) {
        int races = results.size();
        int wins = 0;
        int podiums = 0;
        int dnfs = 0;

        for (ResultsRepository.RaceResult rr : results) {
            Integer pos = rr.finishPosition();

            if (pos == null) {
                dnfs++;
                continue;
            }
            if (pos == 1) wins++;
            if (pos <= 3) podiums++;
        }

        return new DriverAggregate(driverId, races, wins, podiums, dnfs);
    }
}