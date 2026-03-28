package com.bear.asset.controller;

import com.bear.asset.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = Map.of(
                "status", "UP",
                "timestamp", Instant.now().toEpochMilli()
        );
        return Result.success(data);
    }
}
