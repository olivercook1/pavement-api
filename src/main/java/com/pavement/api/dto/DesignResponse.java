package com.pavement.api.dto;

import java.util.List;

public class DesignResponse {
    // Existing fields (kept for compatibility)
    private String recommendedStructure;
    private double totalThickness;      // kept; same as asphaltThicknessMm for now
    private String clauseReference;
    private String foundationClass;

    // Explicit meaning
    private double asphaltThicknessMm;

    // NEW: non-blocking messages for transparency
    private List<String> warnings;

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
}
