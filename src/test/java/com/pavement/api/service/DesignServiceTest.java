package com.pavement.api.service;

import com.pavement.api.dto.DesignRequest;
import com.pavement.api.dto.DesignResponse;
import com.pavement.api.domain.Layer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DesignServiceTest {

    private final DesignService service = new DesignService();

    private DesignRequest req(double cbr, double msa, int life, String type) {
        DesignRequest r = new DesignRequest();
        r.setCbr(cbr);
        r.setMsa(msa);                // we drive design by msa directly
        r.setDesignLife(life);
        r.setPavementType(type);
        // trafficCategory left null on purpose (msa provided)
        return r;
    }

    @Test
    void hbgm_rule_msa10_returns_135mm() {
        DesignResponse res = service.calculate(req(5.0, 10.0, 20, "flexible"));
        assertThat(res.getAsphaltThicknessMm()).isEqualTo(135.0);
        assertThat(res.getTotalThickness()).isEqualTo(135.0);
    }

    @Test
    void hbgm_rule_upper_bound_msa100_returns_180mm() {
        DesignResponse res = service.calculate(req(8.0, 100.0, 40, "flexible"));
        assertThat(res.getAsphaltThicknessMm()).isEqualTo(180.0);
        // layer minima for heavy traffic should be respected
        assertThat(res.getLayers()).extracting(Layer::getName).contains("Surface", "Binder", "Base (asphalt)");
        double sum = res.getLayers().stream().mapToDouble(Layer::getThicknessMm).sum();
        assertThat(sum).isEqualTo(res.getAsphaltThicknessMm());
        // heavy-traffic minima (you set these in the service)
        double surf = res.getLayers().stream().filter(l -> l.getName().equals("Surface")).findFirst().get().getThicknessMm();
        double bind = res.getLayers().stream().filter(l -> l.getName().equals("Binder")).findFirst().get().getThicknessMm();
        assertThat(surf).isGreaterThanOrEqualTo(50.0);
        assertThat(bind).isGreaterThanOrEqualTo(80.0);
    }

    @Test
    void hbgm_rule_floor_at_100mm_for_msa1() {
        DesignResponse res = service.calculate(req(8.0, 1.0, 20, "flexible"));
        assertThat(res.getAsphaltThicknessMm()).isEqualTo(100.0); // clamp + round-up to nearest 5
    }

    @Test
    void layers_are_multiple_of_5_and_sum_to_total() {
        DesignResponse res = service.calculate(req(5.0, 10.0, 20, "flexible"));
        double sum = res.getLayers().stream().mapToDouble(Layer::getThicknessMm).sum();
        assertThat(sum).isEqualTo(res.getAsphaltThicknessMm());
        // all layers multiple of 5
        assertThat(res.getLayers()).allSatisfy(l ->
                assertThat(l.getThicknessMm() % 5.0).isEqualTo(0.0)
        );
    }

    @Test
    void foundation_class_from_cbr5_is_fc1() {
        DesignResponse res = service.calculate(req(5.0, 10.0, 20, "flexible"));
        assertThat(res.getFoundationClass()).isEqualTo("FC1");
    }
}
