package com.example.BTL_25_4.service;

import com.example.BTL_25_4.dto.BookingDetailsDTO;
import com.example.BTL_25_4.dto.MultiBookingRequestDTO; // MỚI: DTO cho yêu cầu đặt nhiều xe
import com.example.BTL_25_4.dto.BookingResultDTO;     // MỚI: DTO cho kết quả của từng xe khi đặt nhiều
import com.example.BTL_25_4.entity.Booking;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface định nghĩa các dịch vụ liên quan đến việc đặt xe.
 */
public interface BookingService {
    /**
     * Tạo một đơn đặt xe mới.
     * @param bookingRequestData Dữ liệu yêu cầu đặt xe.
     * @return Đơn đặt xe đã được tạo.
     * @throws RuntimeException Nếu có lỗi xảy ra trong quá trình xử lý (ví dụ: xe không có sẵn, ngày không hợp lệ).
     */
    Booking createBooking(Booking bookingRequestData);

    /**
     * Lấy danh sách các khoảng ngày đã được đặt cho một xe cụ thể.
     * Chỉ lấy các booking đang hoạt động (chưa bị hủy và ngày kết thúc chưa qua).
     * @param carId ID của xe.
     * @return Danh sách các Map, mỗi Map chứa "startDate" và "endDate".
     */
    List<Map<String, String>> getBookedDateMapsForCar(Long carId);

    /**
     * Tìm chi tiết một đơn đặt xe cụ thể dựa theo ID.
     * @param bookingId ID của đơn đặt xe.
     * @return Optional chứa BookingDetailsDTO nếu tìm thấy, ngược lại là Optional rỗng.
     */
    Optional<BookingDetailsDTO> findBookingDetailsById(Long bookingId);

    /**
     * Xử lý yêu cầu đặt nhiều xe cùng một lúc.
     * @param multiBookingRequest DTO chứa thông tin khách hàng, ngày đặt và danh sách ID các xe muốn đặt.
     * @return Danh sách các BookingResultDTO, mỗi DTO biểu diễn kết quả của việc đặt một xe.
     */
    List<BookingResultDTO> createMultipleBookings(MultiBookingRequestDTO multiBookingRequest); // PHƯƠNG THỨC MỚI
}