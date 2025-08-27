package com.pavement.api.controller;

import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import com.pavement.api.service.DesignService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;


@RestController
public class DesignController {

    private final DesignService designService;

    public DesignController(DesignService designService) {
        this.designService = designService;
    }
    
    @Operation(operationId = "designCalculate", summary = "Calculate pavement design",
               description = "Uses CD226 for asphalt and CD225 restricted figures for foundation (subbase/capping).")
    @PostMapping("/api/design/calculate")
    public DesignResponse calculate(@Valid @RequestBody DesignRequest req) {
        return designService.calculate(req);
    }
}
