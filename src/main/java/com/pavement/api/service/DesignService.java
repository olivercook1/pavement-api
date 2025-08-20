package com.pavement.api.service;

import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import com.pavement.api.domain.Layer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DesignService {

    public DesignResponse calculate(DesignRequest req) {

        // Collect non-blocking warnings
        List<String> warnings = new ArrayList<>();

        // ---- 1) Design traffic (msa) ----
        double Tmsa = (req.getMsa() != null) ? req.getMsa() : mapCategoryToMsa(req.getTrafficCategory());
        // Note if we approximated from category
        if (req.getMsa() == null && req.getTrafficCategory() != null) {
            warnings.add("Traffic category \"" + req.getTrafficCategory() + "\" approximated to "
                    + Tmsa + " msa for prototype. Provide msa for exact design.");
        }
        if (Tmsa < 1.0) Tmsa = 1.0;
        if (Tmsa > 400.0) Tmsa = 400.0;

        // ---- 2) Foundation class from CBR (CD 225) ----
        // E = 17.6 * CBR^0.64  [nominal mapping valid ~2–12% CBR]
        double cbr = req.getCbr();
        double E = 17.6 * Math.pow(cbr, 0.64);
        if (cbr < 2.0 || cbr > 12.0) {
            warnings.add("CBR→E mapping nominally valid for ~2–12% CBR; interpret foundation class with caution (CBR=" + cbr + ").");
        }
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
                warnings.add("CD 226 rule applied: T ≥ 80 msa ⇒ asphalt thickness set to 180 mm.");
            } else {
                // Eq 2.24
                double logT = Math.log10(Tmsa);
                H = -16.05 * (logT * logT) + 101.0 * logT + 45.8;
                // clamp to 100–180 mm
                if (H < 100.0) H = 100.0;
                if (H > 180.0) H = 180.0;
            }
            // CD 226: round UP to the nearest 5 mm
            asphaltThicknessMm = ceil5(H);
        } else {
            asphaltThicknessMm = 0.0; // other pavement types later
            warnings.add("Pavement type \"" + req.getPavementType() + "\" not yet implemented in this prototype.");
        }

        // --- Simple default asphalt layer split tied to traffic ---
        List<Layer> layers = new ArrayList<>();

        // minima by traffic
        double minSurf = (Tmsa >= 80.0) ? 50.0 : 40.0;
        double minBind = (Tmsa >= 80.0) ? 80.0 : 60.0;

        // start with minima
        double surf = minSurf;
        double bind = minBind;
        double base = asphaltThicknessMm - (surf + bind);

        // if base went negative, relax binder to 60, then surface to 40, re-evaluate
        if (base < 0) {
            bind = Math.min(bind, 60.0);
            base = asphaltThicknessMm - (surf + bind);
        }
        if (base < 0) {
            surf = Math.min(surf, 40.0);
            base = asphaltThicknessMm - (surf + bind);
        }

        // round up each to 5 mm and re-balance to keep total exactly H
        surf = ceil5(surf);
        bind = ceil5(bind);
        base = asphaltThicknessMm - (surf + bind);
        if (base < 0) base = 0; // guard
        base = ceil5(base);

        // if rounding pushed the sum over H, trim base down (never below 0)
        double sum = surf + bind + base;
        if (sum > asphaltThicknessMm) {
            double overflow = sum - asphaltThicknessMm;
            double newBase = base - overflow;
            base = Math.max(0, newBase); // keep exact trim (don’t re-round here)
        }

        // build list (only include non-zero layers)
        if (surf > 0) layers.add(new Layer("Surface", "SMA 10 surf", surf));
        if (bind > 0) layers.add(new Layer("Binder", "AC 20 dense bin", bind));
        if (base > 0) layers.add(new Layer("Base (asphalt)", "AC 32 dense base", base));

        // advisory about materials/minima
        warnings.add("Layer split uses conservative defaults; verify materials and layer minima to your project spec / CD 226 tables.");

        // ---- 4) Build result ----
        DesignResponse res = new DesignResponse();
        res.setRecommendedStructure("Flexible (HBGM base), " + foundationClass);
        // asphalt thickness above HBGM base per CD 226 Eq 2.24
        res.setAsphaltThicknessMm(asphaltThicknessMm);
        // keep totalThickness equal (for now) for compatibility with the UI
        res.setTotalThickness(asphaltThicknessMm);
        res.setFoundationClass(foundationClass);
        res.setClauseReference("CD 226 Eq 2.24; notes to Fig/Table; CD 225 CBR→E.");
        res.setWarnings(warnings);
        res.setLayers(layers);
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

    private static double ceil5(double x) {
        return Math.ceil(x / 5.0) * 5.0;
    }
}
