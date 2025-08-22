package com.pavement.api.engine.cd225;

import java.util.List;

public final class Cd225Restricted {
	private Cd225Restricted() {
	}

	public enum Scheme {
		FC1_CAP_ONLY,

		// FC2
		FC2_SUBBASE_ONLY_UNBOUND, FC2_SUBBASE_ONLY_BOUND, FC2_SUBBASE_ON_CAP_UNBOUND, FC2_SUBBASE_ON_CAP_BOUND,
		FC2_SUBBASE_ON_BOUND_CAP_UNBOUND, FC2_SUBBASE_ON_BOUND_CAP_BOUND,

		// FC3
		FC3_SUBBASE_ONLY_UNBOUND, FC3_SUBBASE_ONLY_BOUND, FC3_SUBBASE_ON_CAP_UNBOUND, FC3_SUBBASE_ON_CAP_BOUND,
		FC3_SUBBASE_ON_BOUND_CAP_UNBOUND, FC3_SUBBASE_ON_BOUND_CAP_BOUND
	}

	public record FoundationComponents(int cappingMm, int subbaseMm) {
	}

	// ======= FIGURE DATA (E MPa, thickness mm) =======

	// FC1 — capping only (Figure 3.17)
	private static final double[][] FC1_CAP = { { 30, 570 }, { 40, 470 }, { 50, 400 }, { 60, 350 }, { 70, 300 },
			{ 80, 260 }, { 90, 230 }, { 100, 200 }, { 110, 200 }, { 120, 200 }, { 130, 200 }, { 140, 200 },
			{ 150, 200 } };

	// FC2 — SUBBASE ONLY — UNBOUND (803/804/806/807) — Figure 3.18 (SUBBASE curve
	// only)
	private static final double[][] FC2_SO_SUBBASE_UNBOUND = { { 30, 410 }, { 40, 330 }, { 50, 270 }, { 60, 250 },
			{ 70, 230 }, { 80, 220 }, { 90, 210 }, { 100, 200 }, { 110, 200 }, { 120, 200 }, { 130, 200 }, { 140, 200 },
			{ 150, 200 } };

	// FC2 — SUBBASE ONLY — BOUND (821/822/840, Rc ≥ C3/4) — Figure 3.18 (SUBBASE
	// curve only)
	private static final double[][] FC2_SO_SUBBASE_BOUND = { { 30, 300 }, { 40, 230 }, { 50, 180 }, { 60, 170 },
			{ 70, 170 }, { 80, 170 }, { 90, 170 }, { 100, 170 }, { 110, 170 }, { 120, 170 }, { 130, 170 }, { 140, 170 },
			{ 150, 170 } };

	// FC2 — SUBBASE ON CAPPING — CAPPING (same for unbound/bound) — Figure 3.19
	private static final double[][] FC2_SOC_CAPPING = { { 30, 430 }, { 40, 340 }, { 50, 250 }, { 60, 230 }, { 70, 210 },
			{ 80, 190 }, { 90, 170 }, { 100, 150 }, { 110, 150 }, { 120, 150 }, { 130, 150 }, { 140, 150 },
			{ 150, 150 } };

	// FC2 — SUBBASE ON CAPPING — SUBBASE — Figure 3.19
	private static final double[][] FC2_SOC_SUBBASE_UNBOUND = { { 30, 250 }, { 40, 220 }, { 50, 200 }, { 60, 170 },
			{ 70, 150 }, { 80, 150 }, { 90, 150 }, { 100, 150 }, { 110, 150 }, { 120, 150 }, { 130, 150 }, { 140, 150 },
			{ 150, 150 } };
	private static final double[][] FC2_SOC_SUBBASE_BOUND = { { 30, 150 }, { 40, 150 }, { 50, 150 }, { 60, 150 },
			{ 70, 150 }, { 80, 150 }, { 90, 150 }, { 100, 150 }, { 110, 150 }, { 120, 150 }, { 130, 150 }, { 140, 150 },
			{ 150, 150 } };

	// FC2 — SUBBASE ON BOUND CAPPING — CAPPING (bound capping) — Figure 3.22
	private static final double[][] FC2_SOBC_CAPPING = { { 30, 280 }, { 40, 280 }, { 50, 280 }, { 60, 280 },
			{ 70, 280 }, { 80, 280 }, { 90, 280 }, { 100, 280 }, { 110, 280 }, { 120, 280 }, { 130, 280 }, { 140, 280 },
			{ 150, 280 } };

	// FC2 — SUBBASE ON BOUND CAPPING — SUBBASE — Figure 3.22
	private static final double[][] FC2_SOBC_SUBBASE_UNBOUND = { { 30, 280 }, { 40, 230 }, { 50, 190 }, { 60, 160 },
			{ 70, 150 }, { 80, 150 }, { 90, 150 }, { 100, 150 }, { 110, 150 }, { 120, 150 }, { 130, 150 }, { 140, 150 },
			{ 150, 150 } };
	private static final double[][] FC2_SOBC_SUBBASE_BOUND = { { 30, 150 }, { 40, 150 }, { 50, 150 }, { 60, 150 },
			{ 70, 150 }, { 80, 150 }, { 90, 150 }, { 100, 150 }, { 110, 150 }, { 120, 150 }, { 130, 150 }, { 140, 150 },
			{ 150, 150 } };

	// ==== FC3 — SUBBASE ONLY (Figure 3.19) ====
	private static final double[][] FC3_SO_SUBBASE_UNBOUND = { { 30, 370 }, { 40, 320 }, { 50, 290 }, { 60, 260 },
			{ 70, 240 }, { 80, 210 }, { 90, 200 }, { 100, 200 }, { 110, 200 }, { 120, 200 }, { 130, 200 }, { 140, 200 },
			{ 150, 200 } };
	private static final double[][] FC3_SO_SUBBASE_BOUND = { { 30, 260 }, { 40, 230 }, { 50, 200 }, { 60, 200 },
			{ 70, 200 }, { 80, 200 }, { 90, 200 }, { 100, 200 }, { 110, 200 }, { 120, 200 }, { 130, 200 }, { 140, 200 },
			{ 150, 200 } };

	// ==== FC3 — SUBBASE ON CAPPING (Figure 3.21) ====
	// CAPPING curve (same numbers as FC2 SOC)
	private static final double[][] FC3_SOC_CAPPING = { { 30, 430 }, { 40, 340 }, { 50, 250 }, { 60, 230 }, { 70, 210 },
			{ 80, 190 }, { 90, 170 }, { 100, 150 }, { 110, 150 }, { 120, 150 }, { 130, 150 }, { 140, 150 },
			{ 150, 150 } };
	// SUBBASE curves
	private static final double[][] FC3_SOC_SUBBASE_UNBOUND = { { 30, 320 }, { 40, 290 }, { 50, 270 }, { 60, 250 },
			{ 70, 230 }, { 80, 210 }, { 90, 200 }, { 100, 180 }, { 110, 180 }, { 120, 180 }, { 130, 180 }, { 140, 180 },
			{ 150, 180 } };
	private static final double[][] FC3_SOC_SUBBASE_BOUND = { { 30, 230 }, { 40, 210 }, { 50, 190 }, { 60, 180 },
			{ 70, 170 }, { 80, 170 }, { 90, 170 }, { 100, 170 }, { 110, 170 }, { 120, 170 }, { 130, 170 }, { 140, 170 },
			{ 150, 170 } };

	// ==== FC3 — SUBBASE ON BOUND CAPPING (Figure 3.23) ====
	// CAPPING is constant 280 mm
	private static final double[][] FC3_SOBC_CAPPING = { { 30, 280 }, { 40, 280 }, { 50, 280 }, { 60, 280 },
			{ 70, 280 }, { 80, 280 }, { 90, 280 }, { 100, 280 }, { 110, 280 }, { 120, 280 }, { 130, 280 }, { 140, 280 },
			{ 150, 280 } };
	// SUBBASE curves
	private static final double[][] FC3_SOBC_SUBBASE_UNBOUND = { { 30, 330 }, { 40, 290 }, { 50, 270 }, { 60, 240 },
			{ 70, 230 }, { 80, 220 }, { 90, 210 }, { 100, 200 }, { 110, 190 }, { 120, 180 }, { 130, 180 }, { 140, 170 },
			{ 150, 170 } };
	private static final double[][] FC3_SOBC_SUBBASE_BOUND = { { 30, 240 }, { 40, 210 }, { 50, 190 }, { 60, 180 },
			{ 70, 170 }, { 80, 170 }, { 90, 170 }, { 100, 170 }, { 110, 170 }, { 120, 170 }, { 130, 170 }, { 140, 170 },
			{ 150, 170 } };

	// ======= Public helpers =======

	/**
	 * Used by DesignService (current flow): choose a default scheme by traffic and
	 * return capping only.
	 */
	// Back-compat: return capping only; default to SUBBASE_ON_CAP_UNBOUND for
	// FC2/FC3
	public static double cappingFor(double cbr, double msa, List<String> warnings) {
		Scheme scheme = (msa <= 20.0) ? Scheme.FC1_CAP_ONLY
				: (msa <= 80.0) ? Scheme.FC2_SUBBASE_ON_CAP_UNBOUND : Scheme.FC3_SUBBASE_ON_CAP_UNBOUND;
		return compute(cbr, scheme, warnings).cappingMm();
	}

	/** Full calculator: returns both capping and subbase for an explicit scheme. */
	public static FoundationComponents compute(double cbr, Scheme scheme, List<String> warnings) {
		final double E = 17.6 * Math.pow(cbr, 0.64); // MPa

		double[][] capPts = null, subPts = null;
		switch (scheme) {
		case FC1_CAP_ONLY -> {
			capPts = FC1_CAP;
			subPts = null;
		}

		case FC2_SUBBASE_ONLY_UNBOUND -> {
			capPts = null;
			subPts = FC2_SO_SUBBASE_UNBOUND;
		}
		case FC2_SUBBASE_ONLY_BOUND -> {
			capPts = null;
			subPts = FC2_SO_SUBBASE_BOUND;
		}

		case FC2_SUBBASE_ON_CAP_UNBOUND -> {
			capPts = FC2_SOC_CAPPING;
			subPts = FC2_SOC_SUBBASE_UNBOUND;
		}
		case FC2_SUBBASE_ON_CAP_BOUND -> {
			capPts = FC2_SOC_CAPPING;
			subPts = FC2_SOC_SUBBASE_BOUND;
		}

		case FC2_SUBBASE_ON_BOUND_CAP_UNBOUND -> {
			capPts = FC2_SOBC_CAPPING;
			subPts = FC2_SOBC_SUBBASE_UNBOUND;
		}
		case FC2_SUBBASE_ON_BOUND_CAP_BOUND -> {
			capPts = FC2_SOBC_CAPPING;
			subPts = FC2_SOBC_SUBBASE_BOUND;
		}

		case FC3_SUBBASE_ONLY_UNBOUND -> {
			capPts = null;
			subPts = FC3_SO_SUBBASE_UNBOUND;
		}
		case FC3_SUBBASE_ONLY_BOUND -> {
			capPts = null;
			subPts = FC3_SO_SUBBASE_BOUND;
		}

		case FC3_SUBBASE_ON_CAP_UNBOUND -> {
			capPts = FC3_SOC_CAPPING;
			subPts = FC3_SOC_SUBBASE_UNBOUND;
		}
		case FC3_SUBBASE_ON_CAP_BOUND -> {
			capPts = FC3_SOC_CAPPING;
			subPts = FC3_SOC_SUBBASE_BOUND;
		}

		case FC3_SUBBASE_ON_BOUND_CAP_UNBOUND -> {
			capPts = FC3_SOBC_CAPPING;
			subPts = FC3_SOBC_SUBBASE_UNBOUND;
		}
		case FC3_SUBBASE_ON_BOUND_CAP_BOUND -> {
			capPts = FC3_SOBC_CAPPING;
			subPts = FC3_SOBC_SUBBASE_BOUND;
		}

		}

		double cap = (capPts == null) ? 0 : interpHoldLast(E, capPts);
		double sub = (subPts == null) ? 0 : interpHoldLast(E, subPts);

		// Round up to construction increments (25 mm)
		int capR = (int) (Math.ceil(cap / 25.0) * 25.0);
		int subR = (int) (Math.ceil(sub / 25.0) * 25.0);

		if (capR > 0 || subR > 0) {
			warnings.add(String.format("CD225 restricted (%s, E=%.1f MPa): cap %d mm, subbase %d mm.", scheme.name(), E,
					capR, subR));
		}
		return new FoundationComponents(capR, subR);
	}

	// Linear interpolation; clamp below first point; hold last value above max E.
	private static double interpHoldLast(double E, double[][] pts) {
		if (E <= pts[0][0])
			return pts[0][1];
		for (int i = 1; i < pts.length; i++) {
			double e0 = pts[i - 1][0], c0 = pts[i - 1][1];
			double e1 = pts[i][0], c1 = pts[i][1];
			if (E <= e1) {
				double t = (E - e0) / (e1 - e0);
				return c0 + t * (c1 - c0);
			}
		}
		return pts[pts.length - 1][1];
	}

	// Choose scheme from traffic + optional fc2Option (case-insensitive).
	public static Scheme chooseScheme(double msa, String option, List<String> warnings) {
		if (msa <= 20.0)
			return Scheme.FC1_CAP_ONLY;

		if (msa <= 80.0) {
			if (option == null)
				return Scheme.FC2_SUBBASE_ON_CAP_UNBOUND;
			switch (option.trim().toUpperCase()) {
			case "SUBBASE_ONLY_UNBOUND":
				return Scheme.FC2_SUBBASE_ONLY_UNBOUND;
			case "SUBBASE_ONLY_BOUND":
				return Scheme.FC2_SUBBASE_ONLY_BOUND;
			case "SUBBASE_ON_CAP_UNBOUND":
				return Scheme.FC2_SUBBASE_ON_CAP_UNBOUND;
			case "SUBBASE_ON_CAP_BOUND":
				return Scheme.FC2_SUBBASE_ON_CAP_BOUND;
			case "SUBBASE_ON_BOUND_CAP_UNBOUND":
				return Scheme.FC2_SUBBASE_ON_BOUND_CAP_UNBOUND;
			case "SUBBASE_ON_BOUND_CAP_BOUND":
				return Scheme.FC2_SUBBASE_ON_BOUND_CAP_BOUND;
			default:
				if (warnings != null)
					warnings.add("fc2Option \"" + option + "\" not recognised; using SUBBASE_ON_CAP_UNBOUND.");
				return Scheme.FC2_SUBBASE_ON_CAP_UNBOUND;
			}
		}

		// msa > 80. Use same tokens; default to SUBBASE_ON_CAP_UNBOUND.
		if (option == null)
			return Scheme.FC3_SUBBASE_ON_CAP_UNBOUND;
		switch (option.trim().toUpperCase()) {
		case "SUBBASE_ONLY_UNBOUND":
			return Scheme.FC3_SUBBASE_ONLY_UNBOUND;
		case "SUBBASE_ONLY_BOUND":
			return Scheme.FC3_SUBBASE_ONLY_BOUND;
		case "SUBBASE_ON_CAP_UNBOUND":
			return Scheme.FC3_SUBBASE_ON_CAP_UNBOUND;
		case "SUBBASE_ON_CAP_BOUND":
			return Scheme.FC3_SUBBASE_ON_CAP_BOUND;
		case "SUBBASE_ON_BOUND_CAP_UNBOUND":
			return Scheme.FC3_SUBBASE_ON_BOUND_CAP_UNBOUND;
		case "SUBBASE_ON_BOUND_CAP_BOUND":
			return Scheme.FC3_SUBBASE_ON_BOUND_CAP_BOUND;
		default:
			if (warnings != null)
				warnings.add("fc2Option \"" + option + "\" not recognised; using SUBBASE_ON_CAP_UNBOUND.");
			return Scheme.FC3_SUBBASE_ON_CAP_UNBOUND;
		}
	}

//==== Foundation classification helpers (CD225) ====
	public static record FoundationInfo(String classLabel, double stiffnessMPa) {
	}

	public static FoundationInfo classifyFoundation(double cbr, List<String> warnings) {
		double E = 17.6 * Math.pow(cbr, 0.64); // MPa
		if (cbr < 2.0 || cbr > 12.0) {
			warnings.add("CBR→E mapping nominally valid for ~2–12% CBR; interpret foundation class with caution (CBR="
					+ cbr + ").");
		}
		String fc = (E >= 400.0) ? "FC4" : (E >= 200.0) ? "FC3" : (E >= 100.0) ? "FC2" : "FC1";
		return new FoundationInfo(fc, E);
	}

	/** CD225 3.14: FC1 shall not be used where T > 20 msa → promote to FC2. */
	public static String enforceFc1LimitForTraffic(String foundationClass, double msa, List<String> warnings) {
		if (msa > 20.0 && "FC1".equals(foundationClass)) {
			warnings.add("CD225 3.14: FC1 not permitted for T > 20 msa → promoting recommended label to FC2.");
			return "FC2";
		}
		return foundationClass;
	}

}
