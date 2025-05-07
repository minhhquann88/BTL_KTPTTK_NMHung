package com.example.BTL_25_4.dto;

import java.util.Objects;
public class CarDTO {
    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private int productionYear;
    private double pricePerDay;
    private String description;
    private String imageUrl;
    private boolean available;

    // Constructor không tham số
    public CarDTO() {
    }
    // Constructor (mapping từ Entity)
    public CarDTO(Long id, String brand, String model, String licensePlate, int productionYear,
                  double pricePerDay, String description, String imageUrl, boolean available) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.productionYear = productionYear;
        this.pricePerDay = pricePerDay;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
    }
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarDTO carDTO = (CarDTO) o;
        return productionYear == carDTO.productionYear &&
                Double.compare(carDTO.pricePerDay, pricePerDay) == 0 &&
                available == carDTO.available &&
                Objects.equals(id, carDTO.id) &&
                Objects.equals(brand, carDTO.brand) &&
                Objects.equals(model, carDTO.model) &&
                Objects.equals(licensePlate, carDTO.licensePlate) &&
                Objects.equals(description, carDTO.description) &&
                Objects.equals(imageUrl, carDTO.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brand, model, licensePlate, productionYear, pricePerDay, description, imageUrl, available);
    }

    @Override
    public String toString() {
        return "CarDTO{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", productionYear=" + productionYear +
                ", pricePerDay=" + pricePerDay +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", available=" + available +
                '}';
    }
}