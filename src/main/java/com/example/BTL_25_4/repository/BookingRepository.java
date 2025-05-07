package com.example.BTL_25_4.repository;

import com.example.BTL_25_4.entity.Booking;
import com.example.BTL_25_4.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // XÓA phương thức findByCustomerIdOrderByStartDateDesc(Long customerId);

    List<Booking> findByCarId(Long carId); // Giữ lại

    List<Booking> findByStatus(BookingStatus status); // Giữ lại

    // Phương thức kiểm tra trùng lặp (Giữ nguyên, rất quan trọng)
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.car.id = :carId " +
            "AND b.status <> com.example.BTL_25_4.entity.BookingStatus.CANCELED " +
            "AND b.startDate < :reqEndDate " +
            "AND b.endDate > :reqStartDate")
    boolean existsConflictingBooking(@Param("carId") Long carId,
                                     @Param("reqStartDate") LocalDate reqStartDate,
                                     @Param("reqEndDate") LocalDate reqEndDate);

    // Phương thức tìm các booking trùng lặp (Giữ lại)
    @Query("SELECT b FROM Booking b WHERE b.car.id = :carId " +
            "AND b.status <> com.example.BTL_25_4.entity.BookingStatus.CANCELED " +
            "AND b.startDate < :reqEndDate " +
            "AND b.endDate > :reqStartDate")
    List<Booking> findConflictingBookings(@Param("carId") Long carId,
                                          @Param("reqStartDate") LocalDate reqStartDate,
                                          @Param("reqEndDate") LocalDate reqEndDate);

    // Có thể thêm phương thức tìm theo email hoặc SĐT nếu cần xem lại booking
    List<Booking> findByCustomerEmailOrderByStartDateDesc(String email);
    List<Booking> findByCustomerPhoneNumberOrderByStartDateDesc(String phoneNumber); // Sửa tên phương thức và tên tham số cho rõ ràng

}