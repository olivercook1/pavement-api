package com.pavement.api.dto;

public class DesignResponse {
    private String recommendedStructure;
    private double totalThickness;
    private String clauseReference;

    public DesignResponse() { }

    public DesignResponse(String recommendedStructure, double totalThickness, String clauseReference) {
        this.recommendedStructure = recommendedStructure;
        this.totalThickness = totalThickness;
        this.clauseReference = clauseReference;
    }

    public String getRecommendedStructure() { return recommendedStructure; }
    public void setRecommendedStructure(String recommendedStructure) { this.recommendedStructure = recommendedStructure; }

    public double getTotalThickness() { return totalThickness; }
    public void setTotalThickness(double totalThickness) { this.totalThickness = totalThickness; }

    public String getClauseReference() { return clauseReference; }
    public void setClauseReference(String clauseReference) { this.clauseReference = clauseReference; }
}
