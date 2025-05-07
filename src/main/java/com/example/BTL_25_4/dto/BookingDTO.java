package com.example.BTL_25_4.dto;

import com.example.BTL_25_4.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class BookingDTO {
    private Long id;
    private LocalDateTime bookingDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private BookingStatus status;

    private Long carId;
    private String carBrandModel;
    private String carLicensePlate;

    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Constructor không tham số
    public BookingDTO() {
    }

    // Constructor
    public BookingDTO(Long id, LocalDateTime bookingDate, LocalDate startDate, LocalDate endDate,
                      double totalPrice, BookingStatus status, Long carId, String carBrandModel,
                      String carLicensePlate, Long customerId, String customerName, String customerEmail, String customerPhone) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.carId = carId;
        this.carBrandModel = carBrandModel;
        this.carLicensePlate = carLicensePlate;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCarBrandModel() {
        return carBrandModel;
    }

    public void setCarBrandModel(String carBrandModel) {
        this.carBrandModel = carBrandModel;
    }

    public String getCarLicensePlate() {
        return carLicensePlate;
    }

    public void setCarLicensePlate(String carLicensePlate) {
        this.carLicensePlate = carLicensePlate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDTO that = (BookingDTO) o;
        return Double.compare(that.totalPrice, totalPrice) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(bookingDate, that.bookingDate) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                status == that.status &&
                Objects.equals(carId, that.carId) &&
                Objects.equals(carBrandModel, that.carBrandModel) &&
                Objects.equals(carLicensePlate, that.carLicensePlate) &&
                Objects.equals(customerId, that.customerId) &&
                Objects.equals(customerName, that.customerName) &&
                Objects.equals(customerEmail, that.customerEmail) &&
                Objects.equals(customerPhone, that.customerPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookingDate, startDate, endDate, totalPrice, status, carId, carBrandModel, carLicensePlate, customerId, customerName, customerEmail, customerPhone);
    }

    @Override
    public String toString() {
        return "BookingDTO{" +
                "id=" + id +
                ", bookingDate=" + bookingDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", carId=" + carId +
                ", carBrandModel='" + carBrandModel + '\'' +
                ", carLicensePlate='" + carLicensePlate + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                '}';
    }
}