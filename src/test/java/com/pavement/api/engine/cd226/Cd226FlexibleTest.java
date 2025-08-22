package com.pavement.api.engine.cd226;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class Cd226FlexibleTest {

  @Test
  void asphaltThickness_formula_and_rule() {
    var w = new ArrayList<String>();
    // T=30 msa → ~160 mm (rounded to 5) per Eq 2.24
    assertThat(Cd226Flexible.asphaltThicknessMm(30.0, w)).isEqualTo(160.0);
    // T≥80 msa → hard set 180 mm
    assertThat(Cd226Flexible.asphaltThicknessMm(80.0, w)).isEqualTo(180.0);
    assertThat(Cd226Flexible.asphaltThicknessMm(160.0, w)).isEqualTo(180.0);
  }

  @Test
  void split_layers_rounds_and_respects_minima() {
    var split = Cd226Flexible.splitAsphaltLayers(160.0, 180.0);
    assertThat(split.surfaceMm()).isEqualTo(50.0);
    assertThat(split.binderMm()).isEqualTo(80.0);
    assertThat(split.baseMm()).isEqualTo(50.0); // 180 - 130
  }

  @Test
  void base_min_thickness_for_flexible() {
    assertThat(Cd226Flexible.baseMinThicknessMm("flexible")).isEqualTo(150.0);
    assertThat(Cd226Flexible.baseMinThicknessMm("rigid")).isEqualTo(0.0);
  }
}
