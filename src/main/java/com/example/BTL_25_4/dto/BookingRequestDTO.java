package com.example.BTL_25_4.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

public class BookingRequestDTO {

    @NotNull(message = "ID xe không được để trống")
    private Long carId;

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 100)
    private String customerName;

    @NotBlank(message = "Email khách hàng không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100)
    private String customerEmail;

    @NotBlank(message = "Số điện thoại khách hàng không được để trống")
    @Pattern(regexp = "^\\d{10,11}$", message = "Số điện thoại phải có 10 hoặc 11 chữ số")
    @Size(max = 20)
    private String customerPhone;

    @NotNull(message = "Ngày bắt đầu thuê không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu thuê phải là ngày hiện tại hoặc tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "Ngày trả xe không được để trống")
    @FutureOrPresent(message = "Ngày trả xe phải là ngày hiện tại hoặc tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Size(max = 500, message = "Địa chỉ không quá 500 ký tự")
    private String customerAddress;

    // Constructor không tham số
    public BookingRequestDTO() {
    }

    // Getters and Setters
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRequestDTO that = (BookingRequestDTO) o;
        return Objects.equals(carId, that.carId) &&
                Objects.equals(customerName, that.customerName) &&
                Objects.equals(customerEmail, that.customerEmail) &&
                Objects.equals(customerPhone, that.customerPhone) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(customerAddress, that.customerAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carId, customerName, customerEmail, customerPhone, startDate, endDate, customerAddress);
    }

    @Override
    public String toString() {
        return "BookingRequestDTO{" +
                "carId=" + carId +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", customerAddress='" + customerAddress + '\'' +
                '}';
    }
}