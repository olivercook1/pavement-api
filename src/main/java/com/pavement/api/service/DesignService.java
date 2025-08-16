package com.pavement.api.service;

import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import org.springframework.stereotype.Service;

@Service
public class DesignService {

    /**
     * NOTE: This is a placeholder rule set to demonstrate a working calculation path.
     * It is NOT the CD 226 algorithm. We’ll swap this for standards-based logic later.
     */
    public DesignResponse calculate(DesignRequest req) {
        double cbr = req.getCbr();
        int tc = Integer.parseInt(req.getTrafficCategory()); // validated to 2..5
        int life = req.getDesignLife();
        String type = req.getPavementType(); // validated to flexible|composite|rigid

        // --- 1) Base thickness by CBR (very rough bands, demo only)
        double base;
        if (cbr < 2.5) {
            base = 600;
        } else if (cbr < 5.0) {
            base = 450;
        } else {
            base = 300;
        }

        // --- 2) Asphalt/slab “surfacing” by traffic category (demo)
        double surfacing; // mm of asphalt or equivalent
        switch (tc) {
            case 2 -> surfacing = 150;
            case 3 -> surfacing = 200;
            case 4 -> surfacing = 250;
            case 5 -> surfacing = 300;
            default -> surfacing = 200; // shouldn’t hit due to validation
        }

        // --- 3) Adjust by pavement type (very rough demo factors)
        // Flexible: as-is
        // Composite: slightly thicker surfacing
        // Rigid: rely less on granular base; more on slab
        String structure;
        if ("composite".equals(type)) {
            surfacing += 25;
            structure = "composite pavement (asphalt over cementitious/base)";
        } else if ("rigid".equals(type)) {
            base = Math.max(200, base - 100);
            surfacing += 50;
            structure = "rigid pavement (concrete/slab dominated)";
        } else {
            structure = "flexible pavement with granular base";
        }

        // --- 4) Design life factor (demo: +1% per year above 20, cap +30%)
        double factor = 1.0;
        if (life > 20) {
            factor = Math.min(1.0 + (life - 20) * 0.01, 1.30);
        }

        double total = (base + surfacing) * factor;

        // Round to nearest 10 mm for neat outputs
        double rounded = Math.round(total / 10.0) * 10.0;

        String clause = "CD 226 – placeholder (demo rules only)";
        return new DesignResponse(structure, rounded, clause);
    }
}
