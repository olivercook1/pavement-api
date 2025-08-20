package com.pavement.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
class DesignControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void calculate_validRequest_returns200AndExpectedFields() throws Exception {
        String json = """
          {
            "cbr": 5,
            "msa": 10,
            "designLife": 20,
            "pavementType": "flexible"
          }
        """;

        mvc.perform(post("/api/design/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
           .andExpect(status().isOk())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.recommendedStructure", startsWith("Flexible")))
           .andExpect(jsonPath("$.asphaltThicknessMm", is(135.0)))
           .andExpect(jsonPath("$.foundationClass", is("FC1")))
           .andExpect(jsonPath("$.layers", is(not(empty()))));
    }

    @Test
    void calculate_invalidRequest_returns400WithValidationErrors() throws Exception {
        // deliberately invalid: super-low CBR, bad category, short life, nonsense type
        String bad = """
          {
            "cbr": 0.1,
            "trafficCategory": "9",
            "designLife": 5,
            "pavementType": "banana"
          }
        """;

        mvc.perform(post("/api/design/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bad))
           .andExpect(status().isBadRequest())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.status", is(400)))
           .andExpect(jsonPath("$.error", is("Bad Request")))
           .andExpect(jsonPath("$.path", is("/api/design/calculate")))
           .andExpect(jsonPath("$.errors", is(not(empty()))))
           .andExpect(jsonPath("$.errors[*].field", hasItems("cbr","designLife","pavementType")))
        	.andExpect(jsonPath("$.errors[*].field", not(hasItem("trafficCategory"))));

    }
}
