package com.pavement.api.engine.cd226;

import java.util.Arrays;

public final class AsphaltBaseNomograph {

  public enum Foundation { FC1, FC2, FC3, FC4 }
  public enum Material   { AC_40_60, EME2 }

  // ---------- Middle panel (MSA → y-level 6..1), piecewise-linear on your points ----------
  // Minimal breakpoints taken from your table (ms a, y)
  private static final double[] FC1_X = { 0, 1, 2, 8, 20 };
  private static final double[] FC1_Y = { 6, 5, 4, 3, 2 };

  private static final double[] FC2_X = { 0, 2, 5, 14, 40, 80 };
  private static final double[] FC2_Y = { 6, 5, 4,  3,  2,  1 };

  private static final double[] FC3_X = { 0, 4, 10, 30, 80 };
  private static final double[] FC3_Y = { 6, 5,  4,  3,  2 };

  private static final double[] FC4_X = { 0, 10, 32, 80 };
  private static final double[] FC4_Y = { 6,  5,  4,  3 };

  private static double yFromMiddle(double msa, Foundation fc) {
    switch (fc) {
      case FC1: return interpClamped(FC1_X, FC1_Y, msa);
      case FC2: return interpClamped(FC2_X, FC2_Y, msa);
      case FC3: return interpClamped(FC3_X, FC3_Y, msa);
      case FC4: return interpClamped(FC4_X, FC4_Y, msa);
      default:  throw new IllegalArgumentException("Unknown foundation");
    }
  }

  // ---------- Right panel (y-level → final asphalt mm), interpolate across y then round-up ----------
  // Your step values per y-level (6..1)
  private static final double[] Y_LEVELS = { 6, 5, 4, 3, 2, 1 };

  private static final double[] AC4060_THK = { 200, 200, 240, 280, 320, 360 };
  private static final double[] EME2_THK   = { 200, 200, 200, 230, 270, 300 };

  private static double asphaltFromRight(double y, Material mat) {
    // clamp to [1,6] (“stops at 1”)
    double yc = Math.max(1.0, Math.min(6.0, y));
    double[] t = (mat == Material.AC_40_60) ? AC4060_THK : EME2_THK;
    return interpClamped(Y_LEVELS, t, yc);
  }

  // ---------- Public API ----------
  /** Returns final asphalt thickness (mm) for an asphalt base (AC 40/60 or EME2). */
  public static int asphaltThickness(double msa, Foundation foundation, Material material) {
    double y = yFromMiddle(msa, foundation);
    double mm = asphaltFromRight(y, material);
    return roundUp5(mm); // CD 226: round up to nearest 5 mm
  }

  // ---------- Helpers ----------
  private static double interpClamped(double[] xs, double[] ys, double xq) {
    if (xq <= xs[0]) return ys[0];
    if (xq >= xs[xs.length - 1]) return ys[ys.length - 1];
    int j = Arrays.binarySearch(xs, xq);
    if (j >= 0) return ys[j];
    j = -j - 1;                  // insertion point
    int i = j - 1;
    double t = (xq - xs[i]) / (xs[j] - xs[i]);
    return ys[i] + t * (ys[j] - ys[i]);
  }

  private static int roundUp5(double v) {
    return (int) Math.ceil(v / 5.0) * 5;
  }

  private AsphaltBaseNomograph() {}
}
