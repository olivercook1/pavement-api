package com.pavement.api.domain;

public class Layer {
    private String name;       // e.g., "Surface", "Binder", "Base (asphalt)"
    private String material;   // e.g., "SMA 10 surf", "AC 20 dense bin", "AC 32 dense base"
    private double thicknessMm;

    public Layer() {}

    public Layer(String name, String material, double thicknessMm) {
        this.name = name;
        this.material = material;
        this.thicknessMm = thicknessMm;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public double getThicknessMm() { return thicknessMm; }
    public void setThicknessMm(double thicknessMm) { this.thicknessMm = thicknessMm; }
}
