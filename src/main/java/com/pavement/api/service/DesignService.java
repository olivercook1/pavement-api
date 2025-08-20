package com.pavement.api.service;

import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import org.springframework.stereotype.Service;

@Service
public class DesignService {

    public DesignResponse calculate(DesignRequest req) {

        // ---- 1) Design traffic (msa) ----
        double Tmsa = (req.getMsa() != null) ? req.getMsa() : mapCategoryToMsa(req.getTrafficCategory());
        if (Tmsa < 1.0) Tmsa = 1.0;
        if (Tmsa > 400.0) Tmsa = 400.0;

        // ---- 2) Foundation class from CBR (CD 225) ----
        // E = 17.6 * CBR^0.64  [nominal mapping]
        double E = 17.6 * Math.pow(req.getCbr(), 0.64);
        String foundationClass = (E >= 400.0) ? "FC4"
                                : (E >= 200.0) ? "FC3"
                                : (E >= 100.0) ? "FC2"
                                : "FC1";

        // ---- 3) Flexible with HBGM base: asphalt thickness (CD 226 Eq 2.24) ----
        double asphaltThicknessMm;
        if ("flexible".equalsIgnoreCase(req.getPavementType())) {
            double H;
            if (Tmsa >= 80.0) {
                // CD 226 note: for T >= 80 msa, use 180 mm
                H = 180.0;
            } else {
                // Eq 2.24
                double logT = Math.log10(Tmsa);
                H = -16.05 * (logT * logT) + 101.0 * logT + 45.8;
                // clamp to 100–180 mm
                if (H < 100.0) H = 100.0;
                if (H > 180.0) H = 180.0;
            }
            // CD 226: round UP to the nearest 5 mm
            asphaltThicknessMm = Math.ceil(H / 5.0) * 5.0;
        } else {
            asphaltThicknessMm = 0.0; // other pavement types later
        }


        // ---- 4) Build result ----
        DesignResponse res = new DesignResponse();
        res.setFoundationClass(foundationClass);

        res.setRecommendedStructure("Flexible (HBGM base), " + foundationClass);
        // This is the asphalt thickness above the HBGM base per CD 226 Eq 2.24
        res.setTotalThickness(asphaltThicknessMm);
        res.setClauseReference("CD 226 Eq 2.24; notes to Fig/Table; CD 225 CBR→E.");
        return res;
    }

    private double mapCategoryToMsa(String cat) {
        // Temporary mapping to keep the current UI usable.
        if (cat == null) return 10.0;
        return switch (cat.trim()) {
            case "2" -> 10.0;   // light
            case "3" -> 30.0;   // medium
            case "4" -> 80.0;   // heavy
            case "5" -> 160.0;  // very heavy
            default -> 10.0;
        };
    }
}
