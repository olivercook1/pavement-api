package com.pavement.api.dto;

import java.util.List;
import com.pavement.api.domain.Layer;

public class DesignResponse {
    private String recommendedStructure;
    private double totalThickness;      // kept; same as asphaltThicknessMm for now
    private String clauseReference;
    private String foundationClass;

    private double asphaltThicknessMm;

    private List<String> warnings;

    // NEW: suggested asphalt layer breakdown (above HBGM base)
    private List<Layer> layers;

    public DesignResponse() { }

    public String getRecommendedStructure() { return recommendedStructure; }
    public void setRecommendedStructure(String recommendedStructure) { this.recommendedStructure = recommendedStructure; }

    public double getTotalThickness() { return totalThickness; }
    public void setTotalThickness(double totalThickness) { this.totalThickness = totalThickness; }

    public String getClauseReference() { return clauseReference; }
    public void setClauseReference(String clauseReference) { this.clauseReference = clauseReference; }

    public String getFoundationClass() { return foundationClass; }
    public void setFoundationClass(String foundationClass) { this.foundationClass = foundationClass; }

    public double getAsphaltThicknessMm() { return asphaltThicknessMm; }
    public void setAsphaltThicknessMm(double asphaltThicknessMm) { this.asphaltThicknessMm = asphaltThicknessMm; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public List<Layer> getLayers() { return layers; }
    public void setLayers(List<Layer> layers) { this.layers = layers; }
}
