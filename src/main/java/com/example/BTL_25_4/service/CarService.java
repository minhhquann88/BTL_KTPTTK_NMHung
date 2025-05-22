package com.example.BTL_25_4.service;

import com.example.BTL_25_4.entity.Car;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface CarService {
    List<Car> findAllAvailableCars();
    Optional<Car> findCarById(Long id);
    List<Car> findAvailableCarsByDateRange(LocalDate startDate, LocalDate endDate);
}