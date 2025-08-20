package com.pavement.api.dto;

import com.pavement.api.domain.Layer;
import java.util.List;

public class DesignResponse {

    private String recommendedStructure;
    private String clauseReference;

    // Asphalt (above HBGM base) per CD 226 Eq 2.24
    private double asphaltThicknessMm;

    // Kept for UI compatibility (same as asphaltThicknessMm for now)
    private double totalThickness;

    // Foundation (from CBR→E mapping)
    private String foundationClass;
    private Double foundationStiffnessMPa;

    // Traffic actually used in calc (msa)
    private Double msaUsed;

    // Base type and its nominal minimum thickness
    private String baseType;              // e.g. "HBGM"
    private Double baseMinThicknessMm;    // e.g. 150

    // New: capping advisory (prototype)
    private Boolean cappingRecommended;   // true/false
    private Double  cappingThicknessMm;   // e.g. 150 if recommended

    // New: overall construction thickness (asphalt + base min + capping)
    private Double totalConstructionThicknessMm;

    // Breakdown + notes
    private List<Layer> layers;
    private List<String> warnings;

    // Getters & setters
    
    
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

    public Boolean getCappingRecommended() { return cappingRecommended; }
    public void setCappingRecommended(Boolean cappingRecommended) { this.cappingRecommended = cappingRecommended; }

    public Double getCappingThicknessMm() { return cappingThicknessMm; }
    public void setCappingThicknessMm(Double cappingThicknessMm) { this.cappingThicknessMm = cappingThicknessMm; }

    public Double getTotalConstructionThicknessMm() { return totalConstructionThicknessMm; }
    public void setTotalConstructionThicknessMm(Double totalConstructionThicknessMm) { this.totalConstructionThicknessMm = totalConstructionThicknessMm; }

    public List<Layer> getLayers() { return layers; }
    public void setLayers(List<Layer> layers) { this.layers = layers; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
}
