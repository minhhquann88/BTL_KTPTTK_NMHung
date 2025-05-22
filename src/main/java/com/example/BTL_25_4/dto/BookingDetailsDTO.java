package com.example.BTL_25_4.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDetailsDTO {
    private Long id;
    private String customerFullName;
    private String customerEmail;
    private String customerPhoneNumber;
    private String carBrand;
    private String carModel;
    private String carLicensePlate;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private String status;
    private String notes;
    private LocalDateTime bookingDate;

    // Constructors
    public BookingDetailsDTO() {
    }

    public BookingDetailsDTO(Long id, String customerFullName, String customerEmail, String customerPhoneNumber,
                             String carBrand, String carModel, String carLicensePlate,
                             LocalDate startDate, LocalDate endDate, double totalPrice,
                             String status, String notes, LocalDateTime bookingDate) {
        this.id = id;
        this.customerFullName = customerFullName;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carLicensePlate = carLicensePlate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.notes = notes;
        this.bookingDate = bookingDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarLicensePlate() {
        return carLicensePlate;
    }

    public void setCarLicensePlate(String carLicensePlate) {
        this.carLicensePlate = carLicensePlate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
}