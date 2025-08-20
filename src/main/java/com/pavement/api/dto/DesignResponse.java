package com.pavement.api.dto;

public class DesignResponse {
    // Existing fields
    private String recommendedStructure;
    private double totalThickness;
    private String clauseReference;

    // NEW: explicit foundation class (FC1–FC4 from CD 225)
    private String foundationClass;

    public DesignResponse() { }

    public String getRecommendedStructure() {
        return recommendedStructure;
    }
    public void setRecommendedStructure(String recommendedStructure) {
        this.recommendedStructure = recommendedStructure;
    }

    public double getTotalThickness() {
        return totalThickness;
    }
    public void setTotalThickness(double totalThickness) {
        this.totalThickness = totalThickness;
    }

    public String getClauseReference() {
        return clauseReference;
    }
    public void setClauseReference(String clauseReference) {
        this.clauseReference = clauseReference;
    }

    public String getFoundationClass() {
        return foundationClass;
    }
    public void setFoundationClass(String foundationClass) {
        this.foundationClass = foundationClass;
    }
}
