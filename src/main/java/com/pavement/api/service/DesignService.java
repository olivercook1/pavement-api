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
	    java.util.List<String> warnings = new java.util.ArrayList<>();

	    // ---- 1) Design traffic (msa) ----
	    double Tmsa = (req.getMsa() != null) ? req.getMsa() : mapCategoryToMsa(req.getTrafficCategory());
	    if (req.getMsa() == null && req.getTrafficCategory() != null) {
	        warnings.add("Traffic category \"" + req.getTrafficCategory() + "\" approximated to "
	                + Tmsa + " msa for prototype. Provide msa for exact design.");
	    }
	    if (Tmsa < 1.0) Tmsa = 1.0;
	    if (Tmsa > 400.0) Tmsa = 400.0;

	    // ---- 2) Foundation class from CBR (CD 225, nominal mapping) ----
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
	            H = 180.0; // rule: T >= 80 msa ⇒ 180 mm
	            warnings.add("CD 226 rule applied: T ≥ 80 msa ⇒ asphalt thickness set to 180 mm.");
	        } else {
	            double logT = Math.log10(Tmsa);
	            H = -16.05 * (logT * logT) + 101.0 * logT + 45.8;
	            if (H < 100.0) H = 100.0;
	            if (H > 180.0) H = 180.0;
	        }
	        // round UP to the nearest 5 mm
	        asphaltThicknessMm = Math.ceil(H / 5.0) * 5.0;
	    } else {
	        asphaltThicknessMm = 0.0;
	        warnings.add("Pavement type \"" + req.getPavementType() + "\" not yet implemented in this prototype.");
	    }

	    // ---- 3a) Split asphalt into layers (simple conservative defaults) ----
	    java.util.List<Layer> layers = new java.util.ArrayList<>();
	    double minSurf = (Tmsa >= 80.0) ? 50.0 : 40.0;
	    double minBind = (Tmsa >= 80.0) ? 80.0 : 60.0;

	    double surf = minSurf;
	    double bind = minBind;
	    double base = asphaltThicknessMm - (surf + bind);

	    if (base < 0) { bind = Math.min(bind, 60.0); base = asphaltThicknessMm - (surf + bind); }
	    if (base < 0) { surf = Math.min(surf, 40.0); base = asphaltThicknessMm - (surf + bind); }

	    surf = ceil5(surf);
	    bind = ceil5(bind);
	    base = asphaltThicknessMm - (surf + bind);
	    if (base < 0) base = 0;
	    // Keep base exact to hit the sum; only ceil if positive remainder remains
	    if (base % 5.0 != 0) base = Math.ceil(base / 5.0) * 5.0;

	    double sum = surf + bind + base;
	    if (sum > asphaltThicknessMm) {
	        double overflow = sum - asphaltThicknessMm;
	        double newBase = base - overflow;
	        base = Math.max(0, newBase);
	    }

	    if (surf > 0) layers.add(new Layer("Surface", "SMA 10 surf", surf));
	    if (bind > 0) layers.add(new Layer("Binder", "AC 20 dense bin", bind));
	    if (base > 0) layers.add(new Layer("Base (asphalt)", "AC 32 dense base", base));

	    warnings.add("Layer split uses conservative defaults; verify materials and layer minima to your project spec / CD 226 tables.");

	    // ---- 4) HBGM base minimum and overall total ----
	    final String baseType = "HBGM";
	    final double hbmgBaseMinMm = "flexible".equalsIgnoreCase(req.getPavementType()) ? 150.0 : 0.0;
	    if (hbmgBaseMinMm > 0) {
	        layers.add(new Layer("HBGM base (min)", "HBGM", hbmgBaseMinMm));
	    }
	    double overallTotal = asphaltThicknessMm + hbmgBaseMinMm;

	    // ---- 5) Build response ----
	    DesignResponse res = new DesignResponse();
	    res.setRecommendedStructure("Flexible (HBGM base), " + foundationClass);
	    res.setAsphaltThicknessMm(asphaltThicknessMm);
	    res.setTotalThickness(overallTotal);
	    res.setFoundationClass(foundationClass);
	    res.setFoundationStiffnessMPa(E);          // expose E if you like it
	    res.setMsaUsed(Tmsa);
	    res.setBaseType(baseType);
	    res.setBaseMinThicknessMm(hbmgBaseMinMm);
	    res.setClauseReference("CD 226 Eq 2.24; notes to Fig/Table; CD 225 CBR→E.");
	    res.setWarnings(warnings);
	    res.setLayers(layers);
	    return res;
	}

	// helper
	private static double ceil5(double x) { return Math.ceil(x / 5.0) * 5.0; }

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
