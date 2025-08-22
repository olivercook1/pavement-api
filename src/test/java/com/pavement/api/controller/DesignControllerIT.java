package com.pavement.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class DesignControllerIT {

  @Autowired private MockMvc mvc;

  @Test
  void fc3_soc_bound_subbase_shapes_and_values() throws Exception {
    String body = """
    {"cbr":2.0,"msa":160,"fc2Option":"SUBBASE_ON_CAP_BOUND","designLife":20,"pavementType":"flexible"}
    """;
    mvc.perform(post("/api/design/calculate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.asphaltThicknessMm").value(180.0))
      .andExpect(jsonPath("$.baseMinThicknessMm").value(150.0))
      .andExpect(jsonPath("$.foundationScheme").value("FC3_SUBBASE_ON_CAP_BOUND"))
      .andExpect(jsonPath("$.cappingThicknessMm").value(450.0))
      .andExpect(jsonPath("$.subbaseThicknessMm").value(250.0));
  }

  @Test
  void fc3_sobc_unbound_values() throws Exception {
    String body = """
    {"cbr":2.0,"msa":160,"fc2Option":"SUBBASE_ON_BOUND_CAP_UNBOUND","designLife":20,"pavementType":"flexible"}
    """;
    mvc.perform(post("/api/design/calculate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.foundationScheme").value("FC3_SUBBASE_ON_BOUND_CAP_UNBOUND"))
      .andExpect(jsonPath("$.cappingThicknessMm").value(300.0))
      .andExpect(jsonPath("$.subbaseThicknessMm").value(350.0));
  }

  @Test
  void fc2_soc_unbound_values() throws Exception {
    String body = """
    {"cbr":2.0,"msa":30,"designLife":20,"pavementType":"flexible"}
    """;
    mvc.perform(post("/api/design/calculate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.foundationScheme").value("FC2_SUBBASE_ON_CAP_UNBOUND"))
      .andExpect(jsonPath("$.cappingThicknessMm").value(450.0))
      .andExpect(jsonPath("$.subbaseThicknessMm").value(250.0));
  }
}
