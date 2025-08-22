package com.pavement.api.service;

import com.pavement.api.domain.Layer;
import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DesignServiceTest {

    private final DesignService service = new DesignService();

    private DesignRequest req(double cbr, double msa, int life, String type) {
        DesignRequest r = new DesignRequest();
        r.setCbr(cbr);
        r.setMsa(msa);                // we drive by msa directly now
        r.setDesignLife(life);
        r.setPavementType(type);
        return r;
    }

    // Helper: sum only asphalt layers (Surface/Binder/Base (asphalt))
    private static double sumAsphalt(DesignResponse res) {
        return res.getLayers().stream()
                .filter(l -> l.getName().equals("Surface")
                        || l.getName().equals("Binder")
                        || l.getName().startsWith("Base (asphalt)"))
                .mapToDouble(Layer::getThicknessMm)
                .sum();
    }

    // Helper: sum all layers in the response
    private static double sumAll(DesignResponse res) {
        return res.getLayers().stream().mapToDouble(Layer::getThicknessMm).sum();
    }

    @Test
    void hbgm_rule_msa10_cbr5_asphalt135_total285_noCapping() {
        DesignResponse res = service.calculate(req(5.0, 10.0, 20, "flexible"));

        // Asphalt thickness from CD226 Eq 2.24 (rounded up to 5)
        assertThat(res.getAsphaltThicknessMm()).isEqualTo(135.0);

        // FC1 capping from Fig 3.17 @ E≈49.3 MPa → ~404.9 → round up to 425
        assertThat(res.getCappingThicknessMm()).isEqualTo(425.0);

        // Total = asphalt 135 + HBGM 150 + capping 425 = 710
        assertThat(res.getTotalThickness()).isEqualTo(710.0);

        // HBGM + Capping present
        assertThat(res.getLayers()).extracting(Layer::getName)
                .contains("HBGM base (min)", "Capping");

        // Asphalt layers sum equals asphaltThicknessMm
        assertThat(sumAsphalt(res)).isEqualTo(res.getAsphaltThicknessMm());

        // All layers sum equals totalThickness
        assertThat(sumAll(res)).isEqualTo(res.getTotalThickness());
    }


    @Test
    void hbgm_rule_upper_bound_msa100_asphalt180_heavy_minima() {
        DesignResponse res = service.calculate(req(8.0, 100.0, 40, "flexible"));

        // T >= 80 msa ⇒ 180 mm rule
        assertThat(res.getAsphaltThicknessMm()).isEqualTo(180.0);

        // Asphalt layers sum equals asphaltThickness
        assertThat(sumAsphalt(res)).isEqualTo(180.0);

        // Heavy-traffic minima respected
        double surf = res.getLayers().stream().filter(l -> l.getName().equals("Surface")).findFirst().get().getThicknessMm();
        double bind = res.getLayers().stream().filter(l -> l.getName().equals("Binder")).findFirst().get().getThicknessMm();
        assertThat(surf).isGreaterThanOrEqualTo(50.0);
        assertThat(bind).isGreaterThanOrEqualTo(80.0);

        // FC3 “subbase on capping (UNBOUND)” @ E≈66.6 MPa:
        // capping ~216.7 → 225; subbase ~236.8 → 250
        assertThat(res.getCappingThicknessMm()).isEqualTo(225.0);
        assertThat(res.getSubbaseThicknessMm()).isEqualTo(250.0);

        // HBGM + Capping present
        assertThat(res.getLayers()).extracting(Layer::getName)
                .contains("HBGM base (min)", "Capping", "Subbase");

        // All layers sum equals reported totalThickness
        assertThat(sumAll(res)).isEqualTo(res.getTotalThickness());
    }


    @Test
    void hbgm_rule_floor_at_100mm_for_msa1() {
        DesignResponse res = service.calculate(req(8.0, 1.0, 20, "flexible"));
        assertThat(res.getAsphaltThicknessMm()).isEqualTo(100.0); // clamp + round-up
        assertThat(sumAsphalt(res)).isEqualTo(100.0);
    }

    @Test
    void layers_are_multiple_of_5_and_sums_align() {
        DesignResponse res = service.calculate(req(5.0, 10.0, 20, "flexible"));

        // All layers multiple of 5 mm
        assertThat(res.getLayers()).allSatisfy(l ->
                assertThat(l.getThicknessMm() % 5.0).isEqualTo(0.0)
        );

        // Sums align
        assertThat(sumAsphalt(res)).isEqualTo(res.getAsphaltThicknessMm());
        assertThat(sumAll(res)).isEqualTo(res.getTotalThickness());
    }

    @Test
    void foundation_class_from_cbr5_is_fc1() {
        DesignResponse res = service.calculate(req(5.0, 10.0, 20, "flexible"));
        assertThat(res.getFoundationClass()).isEqualTo("FC1");
    }
    
    @Test
    void low_cbr_1_4_adds_225mm_capping_and_total_510() {
        // CBR 1.4, 10 msa, 20-year, flexible
        DesignResponse res = service.calculate(req(1.4, 10.0, 20, "flexible"));

        // FC1: first point (30 MPa) holds left → 570 → round up to 575
        assertThat(res.getCappingThicknessMm()).isEqualTo(575.0);

        // Total = asphalt 135 + HBGM 150 + capping 575 = 860
        assertThat(res.getTotalThickness()).isEqualTo(860.0);

        // Must include a Capping layer of 575 mm
        assertThat(res.getLayers()).extracting(Layer::getName).contains("Capping");
        double capping =
            res.getLayers().stream()
                .filter(l -> l.getName().equals("Capping"))
                .mapToDouble(Layer::getThicknessMm)
                .findFirst()
                .orElse(0.0);
        assertThat(capping).isEqualTo(575.0);
    }

}
