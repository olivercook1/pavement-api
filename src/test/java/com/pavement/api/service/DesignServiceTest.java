package com.pavement.api.service;

import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DesignServiceTest {

    @Test
    void calculate_flexible_validInputs_returnsExpected() {
        // Arrange
        DesignService service = new DesignService();
        DesignRequest req = new DesignRequest();
        req.setCbr(5.0);
        req.setTrafficCategory("3");
        req.setDesignLife(20);
        req.setPavementType("flexible");

        // Act
        DesignResponse res = service.calculate(req);

        // Assert
        assertEquals("flexible pavement with granular base", res.getRecommendedStructure());
        assertEquals(500.0, res.getTotalThickness(), 0.001);
        assertTrue(res.getClauseReference().toLowerCase().contains("cd 226"));
    }
}
