package com.acme.f1ranker.controller;

import com.acme.f1ranker.controller.dto.DriverSuggestionDto;
import com.acme.f1ranker.repository.DriversRepository;
import com.acme.f1ranker.service.DriverSearchService;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/drivers")
public class DriversController {

    private final DriverSearchService driverSearchService;

    public DriversController(DriverSearchService driverSearchService) {
        this.driverSearchService = driverSearchService;
    }

    @GetMapping
    public List<DriverSuggestionDto> search(
            @RequestParam(name = "query", defaultValue = "")
            @Size(max = 50, message = "query too long")
            String query
    ) {
        List<DriversRepository.DriverSuggestion> results = driverSearchService.search(query);

        return results.stream()
                .map(d -> new DriverSuggestionDto(d.id(), d.fullName(), d.nationality(), d.firstSeason(), d.lastSeason()))
                .toList();
    }
}
