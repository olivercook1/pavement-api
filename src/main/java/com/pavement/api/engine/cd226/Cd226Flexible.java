package com.pavement.api.engine.cd226;

import java.util.Arrays;
import java.util.List;

public final class Cd226Flexible {

  private Cd226Flexible() {}

  // ------------------------------------------------------------
  // NEW: Asphalt-base nomograph support (right panel)
  // ------------------------------------------------------------

  /** Foundation classes for the middle panel (y-level 6→1). */
  public enum FoundationClass { FC1, FC2, FC3, FC4 }

  /** Asphalt base materials for the right panel. */
  public enum AsphaltMaterial { AC_40_60, EME2 }

  // Middle panel: piecewise-linear points you supplied (MSA → Y level).
  // (Clamped beyond the last point.)
  private static final double[] FC1_X = { 0, 1, 2, 8, 20, 400 };
  private static final double[] FC1_Y = { 6, 5, 4, 3, 2,   2  };

  private static final double[] FC2_X = { 0, 2, 5, 14, 40, 80, 400 };
  private static final double[] FC2_Y = { 6, 5, 4,  3,  2,  1,   1  };

  private static final double[] FC3_X = { 0, 4, 10, 30, 80, 400 };
  private static final double[] FC3_Y = { 6, 5,  4,  3,  2,  2   };

  private static final double[] FC4_X = { 0, 10, 32, 80, 400 };
  private static final double[] FC4_Y = { 6,  5,  4,  3,  3   };

  private static double yFromMiddle(double msa, FoundationClass fc) {
    return switch (fc) {
      case FC1 -> interpClamped(FC1_X, FC1_Y, msa);
      case FC2 -> interpClamped(FC2_X, FC2_Y, msa);
      case FC3 -> interpClamped(FC3_X, FC3_Y, msa);
      case FC4 -> interpClamped(FC4_X, FC4_Y, msa);
    };
  }

  // Right panel: map Y level (6..1) → final asphalt thickness, then interpolate across Y.
  // (Your step values; we interpolate and then round up to 5 mm.)

    // y must be ascending for interpolation
    private static final double[] Y_LEVELS = { 1, 2, 3, 4, 5, 6 };
    // Reordered to match ascending y (y=1..6)
    private static final double[] AC4060_MM = { 360, 320, 280, 240, 200, 200 };
    private static final double[] EME2_MM   = { 300, 270, 230, 200, 200, 200 };

  
  
  
  
  
  private static double asphaltFromRight(double yLevel, AsphaltMaterial mat) {
    double y = Math.max(1.0, Math.min(6.0, yLevel)); // “stops at 1”
    double[] table = (mat == AsphaltMaterial.AC_40_60) ? AC4060_MM : EME2_MM;
    return interpClamped(Y_LEVELS, table, y);
  }

  /**
   * Flexible pavement with **asphalt base** (AC 40/60 or EME2).
   * Uses middle-panel Y level from MSA+foundation, then right panel mapping to final asphalt mm.
   * Returns thickness rounded UP to the nearest 5 mm.
   */
  public static int asphaltBaseThicknessMm(double Tmsa, FoundationClass foundation, AsphaltMaterial material) {
    double y = yFromMiddle(Tmsa, foundation);
    double mm = asphaltFromRight(y, material);
    return (int) ceil5(mm);
  }

  // ------------------------------------------------------------
  // EXISTING: HBGM base path (left panel via Eq 2.24) — unchanged
  // ------------------------------------------------------------

  /**
   * CD 226 Eq 2.24 for flexible with HBGM base, with T≥80 msa ⇒ 180 mm.
   * Rounds UP to nearest 5 mm and clamps to [100, 180].
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

  // Keep-as-is from your version
  public static double baseMinThicknessMm(String pavementType) {
    return "flexible".equalsIgnoreCase(pavementType) ? 150.0 : 0.0;
  }

  // ---------- helper: monotone piecewise-linear interpolation with clamping ----------
  private static double interpClamped(double[] xs, double[] ys, double xq) {
    if (xq <= xs[0]) return ys[0];
    if (xq >= xs[xs.length - 1]) return ys[ys.length - 1];
    int j = Arrays.binarySearch(xs, xq);
    if (j >= 0) return ys[j];
    j = -j - 1;          // insertion point
    int i = j - 1;
    double t = (xq - xs[i]) / (xs[j] - xs[i]);
    return ys[i] + t * (ys[j] - ys[i]);
  }
}
