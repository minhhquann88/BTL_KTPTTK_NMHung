package com.example.BTL_25_4.repository;

import com.example.BTL_25_4.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Phương thức kiểm tra trùng lặp khi tạo booking mới
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.car.id = :carId " +
            "AND b.status <> 'CANCELED' " +
            "AND b.startDate < :reqEndDate " +  // Ngày bắt đầu của booking < Ngày KẾT THÚC mong muốn + 1 ngày
            "AND b.endDate > :reqStartDate")   // Ngày kết thúc của booking > Ngày BẮT ĐẦU mong muốn
    boolean existsConflictingBooking(@Param("carId") Long carId,
                                     @Param("reqStartDate") LocalDate reqStartDate,
                                     @Param("reqEndDate") LocalDate reqEndDate);

    // Tìm các booking chưa qua ngày kết thúc cho xe
    @Query("SELECT b FROM Booking b WHERE b.car.id = :carId AND b.status <> 'CANCELED' AND b.endDate >= :currentDate")
    List<Booking> findActiveBookingsForCar(@Param("carId") Long carId, @Param("currentDate") LocalDate currentDate);

    // ĐIỀU CHỈNH: Tìm ID các xe có lịch đặt CHỒNG LẤN với khoảng thời gian người dùng yêu cầu
    // Một xe được coi là "đã đặt" nếu có bất kỳ booking nào (không bị hủy) mà:
    // - Ngày bắt đầu của booking <= Ngày kết thúc người dùng yêu cầu
    // - VÀ Ngày kết thúc của booking >= Ngày bắt đầu người dùng yêu cầu
    @Query("SELECT DISTINCT b.car.id FROM Booking b WHERE b.status <> 'CANCELED' " +
            "AND b.startDate <= :userEndDateQuery " +
            "AND b.endDate >= :userStartDateQuery")
    List<Long> findBookedCarIdsOverlappingWithDateRange(
            @Param("userStartDateQuery") LocalDate userStartDateQuery,
            @Param("userEndDateQuery") LocalDate userEndDateQuery
    );
}