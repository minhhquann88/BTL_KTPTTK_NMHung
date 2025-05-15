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

    // Phương thức kiểm tra trùng lặp
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.car.id = :carId " +
            "AND b.status <> 'CANCELED' " +
            "AND b.startDate < :reqEndDate " +
            "AND b.endDate > :reqStartDate")
    boolean existsConflictingBooking(@Param("carId") Long carId,
                                     @Param("reqStartDate") LocalDate reqStartDate,
                                     @Param("reqEndDate") LocalDate reqEndDate);

    // Tìm các booking đang hoạt động (không bị 'CANCELED' và chưa qua ngày kết thúc) cho một xe
    @Query("SELECT b FROM Booking b WHERE b.car.id = :carId AND b.status <> 'CANCELED' AND b.endDate >= :currentDate")
    List<Booking> findActiveBookingsForCar(@Param("carId") Long carId, @Param("currentDate") LocalDate currentDate);
}
