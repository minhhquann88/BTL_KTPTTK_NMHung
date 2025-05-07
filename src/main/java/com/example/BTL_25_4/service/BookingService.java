package com.example.BTL_25_4.service;

import com.example.BTL_25_4.dto.BookingDTO;
import com.example.BTL_25_4.dto.BookingRequestDTO;
import com.example.BTL_25_4.exception.CarNotAvailableException;
import com.example.BTL_25_4.exception.ResourceNotFoundException;
import com.example.BTL_25_4.exception.ValidationException;

import java.util.Optional;

public interface BookingService {
    /**
     * Tạo một đơn đặt xe mới với thông tin khách hàng được cung cấp.
     * @param requestDTO Dữ liệu yêu cầu đặt xe (bao gồm thông tin khách hàng).
     * @return BookingDTO của đơn đặt xe vừa tạo.
     * @throws ResourceNotFoundException nếu không tìm thấy Xe.
     * @throws CarNotAvailableException nếu xe không sẵn có hoặc đã được đặt trong khoảng thời gian yêu cầu.
     * @throws ValidationException nếu ngày bắt đầu sau ngày kết thúc.
     */
    BookingDTO createBooking(BookingRequestDTO requestDTO)
            throws ResourceNotFoundException, CarNotAvailableException, ValidationException;

    Optional<BookingDTO> findBookingById(Long bookingId);
}