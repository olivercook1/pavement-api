package com.pavement.api.service;

import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import org.springframework.stereotype.Service;

@Service
public class DesignService {

	public DesignResponse calculate(DesignRequest req) {

	    // Collect non-blocking warnings
	    java.util.List<String> warnings = new java.util.ArrayList<>();

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
	        asphaltThicknessMm = Math.ceil(H / 5.0) * 5.0;
	    } else {
	        asphaltThicknessMm = 0.0; // other pavement types later
	        warnings.add("Pavement type \"" + req.getPavementType() + "\" not yet implemented in this prototype.");
	    }

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
