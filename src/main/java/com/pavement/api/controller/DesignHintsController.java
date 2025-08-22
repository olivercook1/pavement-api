package com.pavement.api.controller;

import com.pavement.api.dto.DesignHints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/design")
public class DesignHintsController {

  @GetMapping("/hints")
  public DesignHints hints() {
    DesignHints h = new DesignHints();
    h.cbrRange = new DesignHints.Range(0.5, 30.0);
    h.msaRange = new DesignHints.Range(1.0, 400.0);
    h.designLifeRange = new DesignHints.Range(10.0, 60.0);

    h.pavementTypes = List.of("flexible"); // composite/rigid to be added later

    h.foundationOptions = List.of(
        new DesignHints.Option("SUBBASE_ONLY_UNBOUND",         "Subbase only — Unbound"),
        new DesignHints.Option("SUBBASE_ONLY_BOUND",           "Subbase only — Bound (Rc ≥ C3/4)"),
        new DesignHints.Option("SUBBASE_ON_CAP_UNBOUND",       "Subbase on capping — Unbound (default FC2/FC3)"),
        new DesignHints.Option("SUBBASE_ON_CAP_BOUND",         "Subbase on capping — Bound (Rc ≥ C3/4)"),
        new DesignHints.Option("SUBBASE_ON_BOUND_CAP_UNBOUND", "Subbase on bound capping — Unbound"),
        new DesignHints.Option("SUBBASE_ON_BOUND_CAP_BOUND",   "Subbase on bound capping — Bound (Rc ≥ C3/4)")
    );

    // Defaults used by the engine’s chooser
    h.defaultFcForMsaLe20   = "FC1_CAP_ONLY";
    h.defaultFcForMsa20to80 = "FC2_SUBBASE_ON_CAP_UNBOUND";
    h.defaultFcForMsaGt80   = "FC3_SUBBASE_ON_CAP_UNBOUND";

    h.notes = List.of(
        "CD 226 Eq 2.24 sets asphalt; T ≥ 80 msa ⇒ 180 mm asphalt.",
        "CD 225 restricted figures provide subbase/capping by foundation class.",
        "CD 225 3.14: FC1 shall not be used where traffic loading > 20 msa."
    );

    return h;
  }
}
