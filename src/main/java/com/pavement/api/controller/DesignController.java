package com.pavement.api.controller;

import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import com.pavement.api.service.DesignService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DesignController {

    private final DesignService designService;

    public DesignController(DesignService designService) {
        this.designService = designService;
    }

    @PostMapping("/api/design/calculate")
    public DesignResponse calculate(@Valid @RequestBody DesignRequest req) {
        return designService.calculate(req);
    }
}
