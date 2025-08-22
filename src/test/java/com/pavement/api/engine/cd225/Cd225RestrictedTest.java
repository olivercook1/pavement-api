package com.pavement.api.engine.cd225;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test; // JUnit 5

public class Cd225RestrictedTest {

  @Test
  public void fc3_soc_unbound_at_cbr2() {
    List<String> warnings = new ArrayList<>();
    Cd225Restricted.FoundationComponents parts = Cd225Restricted.compute(
        2.0, // CBR -> E ≈ 27.4 MPa
        Cd225Restricted.Scheme.FC3_SUBBASE_ON_CAP_UNBOUND,
        warnings
    );
    assertThat(parts.cappingMm()).isEqualTo(450); // 430 → round up 25
    assertThat(parts.subbaseMm()).isEqualTo(325); // 320 → round up 25
  }
  
  @Test
  public void fc2_soc_unbound_at_cbr2() {
    var warnings = new java.util.ArrayList<String>();
    var parts = Cd225Restricted.compute(
        2.0, // CBR -> E ≈ 27.4 MPa
        Cd225Restricted.Scheme.FC2_SUBBASE_ON_CAP_UNBOUND,
        warnings
    );
    // Fig 3.19 (FC2 SOC): cap 430 → 450; subbase 250 → 250
    org.assertj.core.api.Assertions.assertThat(parts.cappingMm()).isEqualTo(450);
    org.assertj.core.api.Assertions.assertThat(parts.subbaseMm()).isEqualTo(250);
  }

  @Test
  public void fc3_sobc_unbound_at_cbr2() {
    var warnings = new java.util.ArrayList<String>();
    var parts = Cd225Restricted.compute(
        2.0, 
        Cd225Restricted.Scheme.FC3_SUBBASE_ON_BOUND_CAP_UNBOUND,
        warnings
    );
    // Fig 3.23 (FC3 SOBC): cap 280 → 300; subbase 330 → 350
    org.assertj.core.api.Assertions.assertThat(parts.cappingMm()).isEqualTo(300);
    org.assertj.core.api.Assertions.assertThat(parts.subbaseMm()).isEqualTo(350);
  }

}
