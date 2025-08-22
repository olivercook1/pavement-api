package com.pavement.api.engine.cd226;

import java.util.List;

public final class Cd226Flexible {
  private Cd226Flexible() {}

  /** CD 226 Eq 2.24 for flexible with HBGM base, with T≥80 msa ⇒ 180 mm.
   *  Rounds UP to nearest 5 mm and clamps to [100, 180].
   */
  public static double asphaltThicknessMm(double Tmsa, List<String> warnings) {
    double H;
    if (Tmsa >= 80.0) {
      H = 180.0;
      if (warnings != null) warnings.add("CD 226 rule applied: T ≥ 80 msa ⇒ asphalt thickness set to 180 mm.");
    } else {
      double logT = Math.log10(Tmsa);
      H = -16.05 * (logT * logT) + 101.0 * logT + 45.8;
      if (H < 100.0) H = 100.0;
      if (H > 180.0) H = 180.0;
    }
    return ceil5(H);
  }

  /** Split asphalt into Surface / Binder / Base using conservative minima. */
  public static AsphaltSplit splitAsphaltLayers(double Tmsa, double asphaltTotalMm) {
    double minSurf = (Tmsa >= 80.0) ? 50.0 : 40.0;
    double minBind = (Tmsa >= 80.0) ? 80.0 : 60.0;

    double surf = minSurf;
    double bind = minBind;
    double base = asphaltTotalMm - (surf + bind);

    // relax minima if needed
    if (base < 0) { bind = Math.min(bind, 60.0); base = asphaltTotalMm - (surf + bind); }
    if (base < 0) { surf = Math.min(surf, 40.0); base = asphaltTotalMm - (surf + bind); }

    // Round to 5s and keep total = asphaltTotalMm
    surf = ceil5(surf);
    bind = ceil5(bind);
    base = asphaltTotalMm - (surf + bind);
    if (base < 0) base = 0;
    if (base % 5.0 != 0) base = ceil5(base);

    double sum = surf + bind + base;
    if (sum > asphaltTotalMm) {
      double overflow = sum - asphaltTotalMm; // multiple of 5
      base = Math.max(0, base - overflow);
    }

    return new AsphaltSplit(surf, bind, base);
  }

  public static record AsphaltSplit(double surfaceMm, double binderMm, double baseMm) {}

  private static double ceil5(double x) { return Math.ceil(x / 5.0) * 5.0; }
  
  public static double baseMinThicknessMm(String pavementType) {
	  return "flexible".equalsIgnoreCase(pavementType) ? 150.0 : 0.0;
	}

}
