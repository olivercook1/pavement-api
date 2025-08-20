package com.pavement.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pavement.api.domain.Layer;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DesignResponse {
    private String recommendedStructure;
    private String clauseReference;

    // Primary results
    private Double asphaltThicknessMm;
    private Double totalThickness; // keep for UI compatibility

    // Foundation
    private String foundationClass;
    private Double foundationStiffnessMPa;
    private Double msaUsed;

    // Base / substructure
    private String baseType;            // e.g. HBGM
    private Double baseMinThicknessMm;  // e.g. 150

    // Capping (nullable so it hides when not used)
    private Double cappingThicknessMm;           // null if not used
    private Boolean cappingRecommended;          // null if not used
    private Double totalConstructionThicknessMm; // optional duplicate of total

    // Details
    private List<String> warnings;
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

    public Double getMsaUsed() { return msaUsed; }
    public void setMsaUsed(Double msaUsed) { this.msaUsed = msaUsed; }

    public String getBaseType() { return baseType; }
    public void setBaseType(String baseType) { this.baseType = baseType; }

    public Double getBaseMinThicknessMm() { return baseMinThicknessMm; }
    public void setBaseMinThicknessMm(Double baseMinThicknessMm) { this.baseMinThicknessMm = baseMinThicknessMm; }

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
