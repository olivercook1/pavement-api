package com.pavement.api.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonInclude;

/** Provide either msa OR trafficCategory. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DesignRequest {

    @DecimalMin(value = "0.5", message = "CBR must be ≥ 0.5")
    @DecimalMax(value = "30.0", message = "CBR must be ≤ 30")
    private double cbr;

    /** Optional direct traffic in MSA. */
    @DecimalMin(value = "0.0", message = "msa must be ≥ 0")
    private Double msa;

    /** Optional traffic category "2".."5". */
    @Pattern(regexp = "^[2-5]$", message = "trafficCategory must be 2, 3, 4 or 5")
    private String trafficCategory;

    @Min(value = 10, message = "designLife must be ≥ 10")
    @Max(value = 60, message = "designLife must be ≤ 60")
    private int designLife;

    @NotBlank(message = "pavementType is required")
    @Pattern(regexp = "^(flexible|composite|rigid)$",
             message = "pavementType must be flexible, composite, or rigid")
    private String pavementType;

    /** Cross-field check: must provide msa OR trafficCategory. */
   
    
    @AssertTrue(message = "Provide either msa or trafficCategory")
    public boolean isTrafficProvided() {
        return this.msa != null || (this.trafficCategory != null && !this.trafficCategory.isBlank());
    }


    // getters/setters
    public double getCbr() { return cbr; }
    public void setCbr(double cbr) { this.cbr = cbr; }
    public Double getMsa() { return msa; }
    public void setMsa(Double msa) { this.msa = msa; }
    public String getTrafficCategory() { return trafficCategory; }
    public void setTrafficCategory(String trafficCategory) { this.trafficCategory = trafficCategory; }
    public int getDesignLife() { return designLife; }
    public void setDesignLife(int designLife) { this.designLife = designLife; }
    public String getPavementType() { return pavementType; }
    public void setPavementType(String pavementType) { this.pavementType = pavementType; }
}
