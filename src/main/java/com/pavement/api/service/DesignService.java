package com.pavement.api.service;

import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import com.pavement.api.domain.Layer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.pavement.api.engine.cd225.Cd225Restricted;
import com.pavement.api.engine.cd225.Cd225Restricted.Scheme;
import com.pavement.api.engine.cd226.Cd226Flexible;
import com.pavement.api.engine.cd226.Cd226Flexible.FoundationClass;
import com.pavement.api.engine.cd226.Cd226Flexible.AsphaltMaterial;

@Service
public class DesignService {

    public DesignResponse calculate(DesignRequest req) {
        List<String> warnings = new ArrayList<>();

        // ---- 1) Design traffic (msa) ----
        double Tmsa = (req.getMsa() != null) ? req.getMsa() : mapCategoryToMsa(req.getTrafficCategory());
        if (req.getMsa() == null && req.getTrafficCategory() != null) {
            warnings.add("Traffic category \"" + req.getTrafficCategory()
                    + "\" approximated to " + Tmsa + " msa for prototype. Provide msa for exact design.");
        }
        if (Tmsa < 1.0) Tmsa = 1.0;
        if (Tmsa > 400.0) Tmsa = 400.0;

        // ---- 2) Foundation class (CD225) ----
        double cbr = req.getCbr();
        var fInfo = Cd225Restricted.classifyFoundation(cbr, warnings);
        double E = fInfo.stiffnessMPa();
        
     // existing:
        String foundationClass = Cd225Restricted.enforceFc1LimitForTraffic(fInfo.classLabel(), Tmsa, warnings);

        // NEW: prefer request override for nomograph curve if provided (but keep calculated FC in response)
        String fcOverride = req.getFoundationClass();
        String fcForNomograph = (fcOverride != null && !fcOverride.isBlank()) ? fcOverride : foundationClass;

        FoundationClass fcEnum;
        try {
            fcEnum = FoundationClass.valueOf(fcForNomograph);
        } catch (IllegalArgumentException ex) {
            fcEnum = FoundationClass.FC2;
            warnings.add("Unrecognised foundation class \"" + fcForNomograph + "\"; defaulting to FC2 for asphalt-base mapping.");
        }
    

        // ---- 3) Flexible: choose ASPHALT BASE (right panel) if asphaltMaterial is provided; else HBGM Eq 2.24 ----
        double asphaltThicknessMm;
        String baseType;
        double hbgmBaseMinMm = 0.0;      // = 0 for asphalt-base path; set later for HBGM path
        String clauseRef;                // dynamic clause reference
        List<Layer> layers = new ArrayList<>();

        if ("flexible".equalsIgnoreCase(req.getPavementType())) {
            String matStr = req.getAsphaltMaterial(); // "AC_40_60" or "EME2"; null/blank means HBGM path
            boolean useAsphaltBase = (matStr != null && !matStr.isBlank());

            if (useAsphaltBase) {
                AsphaltMaterial mat = "EME2".equalsIgnoreCase(matStr) ? AsphaltMaterial.EME2 : AsphaltMaterial.AC_40_60;
                asphaltThicknessMm = Cd226Flexible.asphaltBaseThicknessMm(Tmsa, fcEnum, mat);
                baseType = "Asphalt base (" + (mat == AsphaltMaterial.EME2 ? "EME2" : "AC 40/60") + ")";
                clauseRef = "CD 226 Figure 2.20 nomograph; CD 225 restricted figures.";
            } else {
                // HBGM path (Eq 2.24)
                asphaltThicknessMm = Cd226Flexible.asphaltThicknessMm(Tmsa, warnings);
                baseType = "HBGM base";
                hbgmBaseMinMm = Cd226Flexible.baseMinThicknessMm(req.getPavementType());
                if (hbgmBaseMinMm > 0) {
                    layers.add(new Layer("HBGM base (min)", "HBGM", hbgmBaseMinMm));
                }
                clauseRef = "CD 226 Eq 2.24; CD 225 restricted figures.";
            }
        } else {
            asphaltThicknessMm = 0.0;
            baseType = "N/A";
            clauseRef = "CD 225 restricted figures.";
            warnings.add("Pavement type \"" + req.getPavementType() + "\" not yet implemented in this prototype.");
        }

        // ---- 3a) Split asphalt into layers (engine) ----
        var split = Cd226Flexible.splitAsphaltLayers(Tmsa, asphaltThicknessMm);
        double surf = split.surfaceMm();
        double bind = split.binderMm();
        double baseAsphalt = split.baseMm();
        if (surf > 0) layers.add(new Layer("Surface", "SMA 10 surf", surf));
        if (bind > 0) layers.add(new Layer("Binder", "AC 20 dense bin", bind));
        if (baseAsphalt > 0) layers.add(new Layer("Base (asphalt)", "AC 32 dense base", baseAsphalt));
        warnings.add("Layer split uses conservative defaults; verify materials and layer minima to your project spec / CD 226 tables.");

        // ---- 4) Foundation (CD225 restricted) — choose scheme by traffic; compute subbase + capping ----
        Scheme scheme = Cd225Restricted.chooseScheme(Tmsa, req.getFc2Option(), warnings);
        var foundation = Cd225Restricted.compute(cbr, scheme, warnings);
        double subbaseMm = foundation.subbaseMm();
        double cappingMm = foundation.cappingMm();

        if (subbaseMm > 0) layers.add(new Layer("Subbase", "Granular subbase (CD225 restricted)", subbaseMm));
        if (cappingMm > 0) layers.add(new Layer("Capping", "Granular capping (CD225 restricted)", cappingMm));

        // ---- Totals including subbase/capping ----
        double overallTotal = asphaltThicknessMm + hbgmBaseMinMm + subbaseMm + cappingMm;

        // ---- 5) Build response ----
        Double subbaseOut = (subbaseMm > 0) ? subbaseMm : null;
        Double cappingOut = (cappingMm > 0) ? cappingMm : null;

        DesignResponse res = new DesignResponse();

        res.setFoundationScheme(String.valueOf(scheme));
        res.setRecommendedStructure("Flexible (" + baseType + "), " + foundationClass);
        res.setClauseReference(clauseRef);

        res.setAsphaltThicknessMm(asphaltThicknessMm);
        res.setTotalThickness(overallTotal);
        res.setFoundationClass(foundationClass);
        res.setFoundationStiffnessMPa(E);
        res.setMsaUsed(Tmsa);
        res.setBaseType(baseType);
        res.setBaseMinThicknessMm(hbgmBaseMinMm);

        res.setSubbaseThicknessMm(subbaseOut);
        res.setCappingThicknessMm(cappingOut);
        res.setCappingRecommended(cappingOut != null ? Boolean.TRUE : null);
        res.setTotalConstructionThicknessMm(overallTotal);

        res.setWarnings(warnings);
        res.setLayers(layers);
        return res;
    }

    // helpers

    // Keep for UI compatibility if trafficCategory is sent instead of msa
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
