package com.pavement.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class HealthController {
	
	@Operation(operationId = "healthCheck", summary = "Health check")
    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
    
   
}
