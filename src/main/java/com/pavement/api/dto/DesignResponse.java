package com.pavement.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pavement.api.domain.Layer;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DesignResponse {
    private String recommendedStructure;
    private String clauseReference;
    
 // Primary results
    @Schema(description = "Total asphalt thickness (mm) from CD 226 Eq 2.24 (rounded to 5).", example = "160")
    private Double asphaltThicknessMm;

    @Schema(description = "Legacy duplicate of totalConstructionThicknessMm.", deprecated = true, example = "1010")
    private Double totalThickness;

    // Foundation
    @Schema(description = "Foundation class from CBR→E mapping (after FC1>20 msa rule).", example = "FC2")
    private String foundationClass;

    @Schema(description = "Foundation stiffness E (MPa) computed from CBR.", example = "27.4")
    private Double foundationStiffnessMPa;

    @Schema(description = "Design traffic used (msa).", example = "30")
    private Double msaUsed;

    @Schema(description = "Which CD225 scheme was used.", example = "FC3_SUBBASE_ON_CAP_UNBOUND")
    private String foundationScheme;
    
    // Base / substructure
    @Schema(description = "Base type used by the design.", example = "HBGM")
    private String baseType;

    @Schema(description = "Minimum base thickness applied (mm).", example = "150")
    private Double baseMinThicknessMm;

    // Subbase (nullable)
    @Schema(description = "Subbase thickness (mm) when applicable; null when not used.", example = "250", nullable = true)
    private Double subbaseThicknessMm;

    // Capping (nullable)
    @Schema(description = "Capping thickness (mm) when applicable; null when not used.", example = "450", nullable = true)
    private Double cappingThicknessMm;

    @Schema(description = "True if capping is recommended/used; null when not applicable.", example = "true", nullable = true)
    private Boolean cappingRecommended;

    // Totals
    @Schema(description = "Total constructed thickness including asphalt + base min + subbase + capping (mm).", example = "1010")
    private Double totalConstructionThicknessMm;

    // Details
    @Schema(description = "Informational warnings/notes from the calculation.", example = "[\"CD225 3.14: FC1 not permitted for T > 20 msa…\"]")
    private List<String> warnings;

    @Schema(description = "Layer breakdown in construction order (top to bottom).")
    private List<Layer> layers;


    
    // Getters / setters
    public String getRecommendedStructure() { return recommendedStructure; }
    public void setRecommendedStructure(String recommendedStructure) { this.recommendedStructure = recommendedStructure; }

    public String getClauseReference() { return clauseReference; }
    public void setClauseReference(String clauseReference) { this.clauseReference = clauseReference; }

    public Double getAsphaltThicknessMm() { return asphaltThicknessMm; }
    public void setAsphaltThicknessMm(Double asphaltThicknessMm) { this.asphaltThicknessMm = asphaltThicknessMm; }

    public Double getTotalThickness() { return totalThickness; }
    public void setTotalThickness(Double totalThickness) { this.totalThickness = totalThickness; }

    public String getFoundationClass() { return foundationClass; }
    public void setFoundationClass(String foundationClass) { this.foundationClass = foundationClass; }

    public Double getFoundationStiffnessMPa() { return foundationStiffnessMPa; }
    public void setFoundationStiffnessMPa(Double foundationStiffnessMPa) { this.foundationStiffnessMPa = foundationStiffnessMPa; }
    
 

    public String getFoundationScheme() { return foundationScheme; }
    public void setFoundationScheme(String foundationScheme) { this.foundationScheme = foundationScheme; }


    public Double getMsaUsed() { return msaUsed; }
    public void setMsaUsed(Double msaUsed) { this.msaUsed = msaUsed; }

    public String getBaseType() { return baseType; }
    public void setBaseType(String baseType) { this.baseType = baseType; }

    public Double getBaseMinThicknessMm() { return baseMinThicknessMm; }
    public void setBaseMinThicknessMm(Double baseMinThicknessMm) { this.baseMinThicknessMm = baseMinThicknessMm; }
    
    public Double getSubbaseThicknessMm() { return subbaseThicknessMm; }
    public void setSubbaseThicknessMm(Double subbaseThicknessMm) { this.subbaseThicknessMm = subbaseThicknessMm; }

    public Double getCappingThicknessMm() { return cappingThicknessMm; }
    public void setCappingThicknessMm(Double cappingThicknessMm) { this.cappingThicknessMm = cappingThicknessMm; }

    public Boolean getCappingRecommended() { return cappingRecommended; }
    public void setCappingRecommended(Boolean cappingRecommended) { this.cappingRecommended = cappingRecommended; }

    public Double getTotalConstructionThicknessMm() { return totalConstructionThicknessMm; }
    public void setTotalConstructionThicknessMm(Double totalConstructionThicknessMm) { this.totalConstructionThicknessMm = totalConstructionThicknessMm; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public List<Layer> getLayers() { return layers; }
    public void setLayers(List<Layer> layers) { this.layers = layers; }
    
}
