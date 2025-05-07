package com.example.BTL_25_4.repository;

import com.example.BTL_25_4.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    /**
     * Tìm tất cả các xe có trạng thái sẵn có (available = true).
     * @param available Trạng thái sẵn có (thường là true).
     * @return Danh sách các xe có sẵn.
     */
    List<Car> findByAvailable(boolean available);

    /**
     * Tìm các xe có sẵn theo hãng xe (không phân biệt hoa thường).
     * @param brand Hãng xe cần tìm.
     * @param available Trạng thái sẵn có.
     * @return Danh sách xe phù hợp.
     */
    List<Car> findByBrandContainingIgnoreCaseAndAvailable(String brand, boolean available);

    /**
     * Tìm các xe có sẵn theo mẫu xe (không phân biệt hoa thường).
     * @param model Mẫu xe cần tìm.
     * @param available Trạng thái sẵn có.
     * @return Danh sách xe phù hợp.
     */
    List<Car> findByModelContainingIgnoreCaseAndAvailable(String model, boolean available);

    /**
     * Tìm các xe có sẵn theo hãng HOẶC mẫu xe (không phân biệt hoa thường).
     * @param brand Hãng xe cần tìm.
     * @param model Mẫu xe cần tìm.
     * @param available Trạng thái sẵn có.
     * @return Danh sách xe phù hợp.
     */
    List<Car> findByBrandContainingIgnoreCaseOrModelContainingIgnoreCaseAndAvailable(String brand, String model, boolean available);

    /**
     * Tìm kiếm các xe có sẵn theo từ khóa (tìm trong hãng hoặc mẫu xe).
     * Sử dụng @Query để linh hoạt hơn trong điều kiện tìm kiếm.
     * @param keyword Từ khóa tìm kiếm.
     * @return Danh sách xe phù hợp.
     */
    @Query("SELECT c FROM Car c WHERE c.available = true AND " +
            "(LOWER(c.brand) LIKE LOWER(concat('%', :keyword, '%')) OR " +
            "LOWER(c.model) LIKE LOWER(concat('%', :keyword, '%')))")
    List<Car> searchAvailableCars(@Param("keyword") String keyword);

    /**
     * Tìm các xe KHÔNG có booking nào (chưa bị hủy) trùng lặp trong khoảng thời gian cho trước.
     * Query này phức tạp và cần join bảng, thường được xử lý ở tầng Service
     * bằng cách lấy danh sách xe rồi kiểm tra từng xe bằng BookingRepository.
     * Tuy nhiên, có thể viết query trực tiếp nếu cần tối ưu:
     */
    @Query("SELECT c FROM Car c WHERE c.available = true AND c.id NOT IN " +
            "(SELECT b.car.id FROM Booking b WHERE b.status <> com.example.BTL_25_4.entity.BookingStatus.CANCELED " +
            "AND b.startDate < :reqEndDate AND b.endDate > :reqStartDate)")
    List<Car> findAvailableCarsForPeriod(@Param("reqStartDate") LocalDate reqStartDate,
                                         @Param("reqEndDate") LocalDate reqEndDate);

}