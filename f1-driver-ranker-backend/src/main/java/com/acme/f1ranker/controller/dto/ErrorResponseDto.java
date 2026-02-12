package com.acme.f1ranker.controller.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponseDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String traceId,
        Map<String, String> fieldErrors
) {
}
