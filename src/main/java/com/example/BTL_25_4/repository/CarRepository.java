package com.example.BTL_25_4.repository;

import com.example.BTL_25_4.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    // Tìm tất cả các xe có trạng thái sẵn có (available = true).
    List<Car> findByAvailable(boolean available);

    // Tìm kiếm các xe có sẵn theo từ khóa (tìm trong hãng hoặc mẫu xe).
    @Query("SELECT c FROM Car c WHERE c.available = true AND " +
            "(LOWER(c.brand) LIKE LOWER(concat('%', :keyword, '%')) OR " +
            "LOWER(c.model) LIKE LOWER(concat('%', :keyword, '%')))")
    List<Car> searchAvailableCars(@Param("keyword") String keyword);

}