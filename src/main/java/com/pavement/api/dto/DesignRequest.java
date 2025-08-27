package com.pavement.api.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/** Provide either msa OR trafficCategory. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DesignRequest {

	@DecimalMin(value = "0.5", message = "CBR must be ≥ 0.5")
	@DecimalMax(value = "30.0", message = "CBR must be ≤ 30")
	@Schema(description = "Subgrade strength CBR (%)", example = "5.0", minimum = "0.5", maximum = "30")
	private double cbr;

	/** Optional direct traffic in MSA. */
	@DecimalMin(value = "0.0", message = "msa must be ≥ 0")
	@Schema(description = "Design traffic in msa. If null, provide trafficCategory instead.", example = "30")
	private Double msa;

	/** Optional traffic category "2".."5". */
	@Pattern(regexp = "^[2-5]$", message = "trafficCategory must be 2, 3, 4 or 5")
	@Schema(description = "Traffic category (string '2'..'5'). Used only if msa is null.", example = "3", allowableValues = {"2","3","4","5"})
	private String trafficCategory;

	@Min(value = 10, message = "designLife must be ≥ 10")
	@Max(value = 60, message = "designLife must be ≤ 60")
	@Schema(description = "Design life (years).", example = "20", minimum = "10", maximum = "60")
	private int designLife;

	@NotBlank(message = "pavementType is required")
	@Pattern(regexp = "^(flexible|composite|rigid)$", message = "pavementType must be flexible, composite, or rigid")
	@Schema(description = "Pavement type.", example = "flexible", allowableValues = {"flexible","composite","rigid"})
	private String pavementType;

	/** Cross-field check: must provide msa OR trafficCategory. */

	@AssertTrue(message = "Provide either msa or trafficCategory")
	public boolean isTrafficProvided() {
		return this.msa != null || (this.trafficCategory != null && !this.trafficCategory.isBlank());
	}

	@Schema(description = "FC2/FC3 scheme selector (case-insensitive). If null: SUBBASE_ON_CAP_UNBOUND.", allowableValues = {
			"SUBBASE_ONLY_UNBOUND", "SUBBASE_ONLY_BOUND", "SUBBASE_ON_CAP_UNBOUND", "SUBBASE_ON_CAP_BOUND",
			"SUBBASE_ON_BOUND_CAP_UNBOUND", "SUBBASE_ON_BOUND_CAP_BOUND" }, example = "SUBBASE_ON_CAP_UNBOUND")
	private String fc2Option;

	public String getFc2Option() {
		return fc2Option;
	}

	public void setFc2Option(String fc2Option) {
		this.fc2Option = fc2Option;
	}

	// getters/setters
	public double getCbr() {
		return cbr;
	}

	public void setCbr(double cbr) {
		this.cbr = cbr;
	}

	public Double getMsa() {
		return msa;
	}

	public void setMsa(Double msa) {
		this.msa = msa;
	}

	public String getTrafficCategory() {
		return trafficCategory;
	}

	public void setTrafficCategory(String trafficCategory) {
		this.trafficCategory = trafficCategory;
	}

	public int getDesignLife() {
		return designLife;
	}

	public void setDesignLife(int designLife) {
		this.designLife = designLife;
	}

	public String getPavementType() {
		return pavementType;
	}

	public void setPavementType(String pavementType) {
		this.pavementType = pavementType;
	}
}
