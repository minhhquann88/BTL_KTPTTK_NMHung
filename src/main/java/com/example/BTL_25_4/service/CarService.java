package com.example.BTL_25_4.service;

import com.example.BTL_25_4.dto.CarDTO;
import java.util.List;
import java.util.Optional;

public interface CarService {
    /**
     * Lấy danh sách tất cả các xe đang sẵn có.
     * @return Danh sách CarDTO.
     */
    List<CarDTO> findAllAvailableCars();

    /**
     * Tìm kiếm xe có sẵn theo từ khóa (hãng hoặc model).
     * @param keyword Từ khóa tìm kiếm.
     * @return Danh sách CarDTO phù hợp.
     */
    List<CarDTO> searchAvailableCars(String keyword);

    /**
     * Tìm kiếm xe theo ID.
     * @param id ID của xe.
     * @return Optional chứa CarDTO nếu tìm thấy.
     */
    Optional<CarDTO> findCarById(Long id);

    /**
     * Tìm các xe có sẵn cho thuê trong một khoảng thời gian cụ thể.
     * @param startDate Ngày bắt đầu thuê.
     * @param endDate Ngày kết thúc thuê.
     * @return Danh sách CarDTO có sẵn trong khoảng thời gian đó.
     */
    List<CarDTO> findAvailableCarsForPeriod(java.time.LocalDate startDate, java.time.LocalDate endDate);
}