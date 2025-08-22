package com.pavement.api.dto;

import java.util.List;

public class DesignHints {

  public static class Range {
    public double min;
    public double max;
    public Range() {}
    public Range(double min, double max) { this.min = min; this.max = max; }
  }

  public static class Option {
    public String value;   // machine token sent in request
    public String label;   // friendly text for UI
    public Option() {}
    public Option(String value, String label) { this.value = value; this.label = label; }
  }

  // Basic ranges
  public Range cbrRange;          // % CBR
  public Range msaRange;          // msa used by CD 226
  public Range designLifeRange;   // years

  // Pavement types implemented (for now)
  public List<String> pavementTypes;

  // FC2/FC3 option tokens (usable in fc2Option)
  public List<Option> foundationOptions;

  // Default scheme per traffic band
  public String defaultFcForMsaLe20;     // e.g. FC1_CAP_ONLY
  public String defaultFcForMsa20to80;   // e.g. FC2_SUBBASE_ON_CAP_UNBOUND
  public String defaultFcForMsaGt80;     // e.g. FC3_SUBBASE_ON_CAP_UNBOUND

  // Clause references + notes for the UI
  public List<String> notes;
}
