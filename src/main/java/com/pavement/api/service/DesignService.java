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

        // --- warnings collector (non-blocking) ---
        List<String> warnings = new ArrayList<>();

        // ---- 1) Design traffic (msa) ----
        double Tmsa = (req.getMsa() != null) ? req.getMsa() : mapCategoryToMsa(req.getTrafficCategory());
        if (req.getMsa() == null && req.getTrafficCategory() != null) {
            warnings.add("Traffic category \"" + req.getTrafficCategory()
                    + "\" approximated to " + Tmsa + " msa for prototype. Provide msa for exact design.");
        }
        if (Tmsa < 1.0) Tmsa = 1.0;
        if (Tmsa > 400.0) Tmsa = 400.0;

        // ---- 2) Foundation class from CBR (CD 225, nominal mapping) ----
        double cbr = req.getCbr();
        double E = 17.6 * Math.pow(cbr, 0.64); // MPa (nominal mapping)
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
                // CD 226 note: for T ≥ 80 msa, use 180 mm
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
            // Round UP to nearest 5 mm
            asphaltThicknessMm = ceil5(H);
        } else {
            asphaltThicknessMm = 0.0; // other pavement types later
            warnings.add("Pavement type \"" + req.getPavementType() + "\" not yet implemented in this prototype.");
        }

        // ---- 3a) Split asphalt into layers (simple conservative defaults) ----
        double minSurf = (Tmsa >= 80.0) ? 50.0 : 40.0;
        double minBind = (Tmsa >= 80.0) ? 80.0 : 60.0;

        double surf = minSurf;
        double bind = minBind;
        double baseAsphalt = asphaltThicknessMm - (surf + bind);

        // relax minima if needed
        if (baseAsphalt < 0) { bind = Math.min(bind, 60.0); baseAsphalt = asphaltThicknessMm - (surf + bind); }
        if (baseAsphalt < 0) { surf = Math.min(surf, 40.0); baseAsphalt = asphaltThicknessMm - (surf + bind); }

        // Round to 5s and keep total = asphaltThicknessMm
        surf = ceil5(surf);
        bind = ceil5(bind);
        baseAsphalt = asphaltThicknessMm - (surf + bind);
        if (baseAsphalt < 0) baseAsphalt = 0;
        if (baseAsphalt % 5.0 != 0) baseAsphalt = ceil5(baseAsphalt);

        double sum = surf + bind + baseAsphalt;
        if (sum > asphaltThicknessMm) {
            double overflow = sum - asphaltThicknessMm; // will be multiple of 5
            baseAsphalt = Math.max(0, baseAsphalt - overflow); // keeps multiples of 5
        }

        List<Layer> layers = new ArrayList<>();
        if (surf > 0) layers.add(new Layer("Surface", "SMA 10 surf", surf));
        if (bind > 0) layers.add(new Layer("Binder", "AC 20 dense bin", bind));
        if (baseAsphalt > 0) layers.add(new Layer("Base (asphalt)", "AC 32 dense base", baseAsphalt));
        warnings.add("Layer split uses conservative defaults; verify materials and layer minima to your project spec / CD 226 tables.");

        // ---- 4) HBGM base minimum and optional capping for low CBR ----
        final String baseType = "HBGM";
        final double hbgmBaseMinMm = "flexible".equalsIgnoreCase(req.getPavementType()) ? 150.0 : 0.0;
        if (hbgmBaseMinMm > 0) {
            layers.add(new Layer("HBGM base (min)", "HBGM", hbgmBaseMinMm));
        }

        // Prototype capping rule for low CBR (placeholder bands; refine later to exact CD225/226 table)
        double cappingMm = cappingForCbr(cbr);
        if (cappingMm > 0) {
            layers.add(new Layer("Capping", "Granular capping (prototype)", cappingMm));
            warnings.add(String.format("Low CBR (%.2f%%): added %.0f mm capping (prototype rule).", cbr, cappingMm));
        }

        double overallTotal = asphaltThicknessMm + hbgmBaseMinMm + cappingMm;

        // ---- 5) Build response ----
        DesignResponse res = new DesignResponse();
        res.setRecommendedStructure("Flexible (HBGM base), " + foundationClass);
        res.setClauseReference("CD 226 Eq 2.24; notes to Fig/Table; CD 225 CBR→E.");
        res.setAsphaltThicknessMm(asphaltThicknessMm);
        res.setTotalThickness(overallTotal);
        res.setFoundationClass(foundationClass);
        res.setFoundationStiffnessMPa(E);
        res.setMsaUsed(Tmsa);
        res.setBaseType(baseType);
        res.setBaseMinThicknessMm(hbgmBaseMinMm);
        res.setWarnings(warnings);
        res.setLayers(layers);
        return res;
    }

    // --- helpers ---
    private static double ceil5(double x) {
        return Math.ceil(x / 5.0) * 5.0;
    }

    /**
     * Placeholder capping bands (to be replaced with exact CD 225/226 table):
     * CBR < 1.0 → 300 mm
     * 1.0–<1.5 → 225 mm
     * 1.5–<2.0 → 150 mm
     * ≥ 2.0 → 0 mm
     */
    private static double cappingForCbr(double cbr) {
        if (cbr < 1.0) return 300.0;
        if (cbr < 1.5) return 225.0;
        if (cbr < 2.0) return 150.0;
        return 0.0;
    }

    // Keep for backward compatibility with UI that sends a category
    private double mapCategoryToMsa(String cat) {
        if (cat == null) return 10.0;
        return switch (cat.trim()) {
            case "2" -> 10.0;
            case "3" -> 30.0;
            case "4" -> 80.0;
            case "5" -> 160.0;
            default -> 10.0;
        };
    }
}
