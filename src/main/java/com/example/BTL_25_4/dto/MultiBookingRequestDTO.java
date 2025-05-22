package com.example.BTL_25_4.dto;

import com.example.BTL_25_4.entity.Customer; // Giả sử có thể sử dụng trực tiếp Customer entity
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) cho yêu cầu đặt nhiều xe cùng một lúc.
 * Chứa thông tin chung về khách hàng, ngày đặt và danh sách các ID xe muốn đặt.
 */
public class MultiBookingRequestDTO {

    @NotNull(message = "Thông tin khách hàng không được để trống.")
    @Valid // Đảm bảo các ràng buộc của Customer cũng được kiểm tra
    private Customer customer; // Thông tin khách hàng, tái sử dụng Customer entity như một phần của DTO

    @NotNull(message = "Ngày nhận xe không được để trống.")
    @FutureOrPresent(message = "Ngày nhận xe phải là ngày hiện tại hoặc trong tương lai.")
    private LocalDate startDate; // Ngày nhận xe

    @NotNull(message = "Ngày trả xe không được để trống.")
    @FutureOrPresent(message = "Ngày trả xe phải là ngày hiện tại hoặc trong tương lai.")
    private LocalDate endDate; // Ngày trả xe

    @NotEmpty(message = "Cần cung cấp ít nhất một ID xe để đặt.")
    private List<Long> carIdsToBook; // Danh sách ID của các xe muốn đặt

    private String notes; // Ghi chú chung cho tất cả các xe trong yêu cầu này

    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public List<Long> getCarIdsToBook() {
        return carIdsToBook;
    }

    public void setCarIdsToBook(List<Long> carIdsToBook) {
        this.carIdsToBook = carIdsToBook;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}