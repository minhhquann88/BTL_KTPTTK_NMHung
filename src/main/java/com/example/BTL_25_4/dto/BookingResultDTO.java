package com.example.BTL_25_4.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) biểu diễn kết quả của một nỗ lực đặt xe riêng lẻ
 * trong một yêu cầu đặt nhiều xe.
 * Chứa thông tin về xe, trạng thái thành công/thất bại, tin nhắn, ID đặt xe (nếu thành công),
 * giá và ngày đặt.
 */
public class BookingResultDTO {
    private Long carId; // ID của xe
    private String carBrand; // Hãng xe
    private String carModel; // Mẫu xe
    private boolean success; // Trạng thái đặt xe thành công hay không
    private String message; // Tin nhắn mô tả kết quả (ví dụ: "Đặt xe thành công!" hoặc chi tiết lỗi)
    private Long bookingId; // ID của đơn đặt xe (null nếu không thành công)
    private Double price;   // Giá thuê cho xe này (null nếu không thành công)
    private LocalDate startDate; // Ngày nhận xe
    private LocalDate endDate; // Ngày trả xe

    // Constructors
    public BookingResultDTO() {}

    public BookingResultDTO(Long carId, String carBrand, String carModel, boolean success, String message, Long bookingId, Double price, LocalDate startDate, LocalDate endDate) {
        this.carId = carId;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.success = success;
        this.message = message;
        this.bookingId = bookingId;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }
    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}