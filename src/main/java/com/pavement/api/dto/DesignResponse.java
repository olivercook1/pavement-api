package com.pavement.api.dto;

import java.util.List;
import com.pavement.api.domain.Layer;

public class DesignResponse {
    // Summary
    private String recommendedStructure;
    private String clauseReference;

    // Asphalt thickness above the HBGM base
    private double asphaltThicknessMm;

    // Kept for compatibility with earlier UI (same as asphaltThicknessMm)
    private double totalThickness;

    // Foundation info
    private String foundationClass;          // FC1–FC4
    private Double foundationStiffnessMPa;   // E from CBR→E mapping (CD 225 proxy)
    private Double msaUsed;                  // design traffic (msa) actually used

    // Base info (note: asphalt thickness excludes this HBGM base)
    private String baseType;                 // e.g., "HBGM"
    private Double baseMinThicknessMm;       // advisory minimum (placeholder/configurable)

    // Transparency
    private List<String> warnings;

    // Suggested asphalt layer split (above base)
    private List<Layer> layers;

    public DesignResponse() {}

    public String getRecommendedStructure() { return recommendedStructure; }
    public void setRecommendedStructure(String recommendedStructure) { this.recommendedStructure = recommendedStructure; }

    public String getClauseReference() { return clauseReference; }
    public void setClauseReference(String clauseReference) { this.clauseReference = clauseReference; }

    public double getAsphaltThicknessMm() { return asphaltThicknessMm; }
    public void setAsphaltThicknessMm(double asphaltThicknessMm) { this.asphaltThicknessMm = asphaltThicknessMm; }

    public double getTotalThickness() { return totalThickness; }
    public void setTotalThickness(double totalThickness) { this.totalThickness = totalThickness; }

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

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public List<Layer> getLayers() { return layers; }
    public void setLayers(List<Layer> layers) { this.layers = layers; }
}
