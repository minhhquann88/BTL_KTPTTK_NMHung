package com.example.BTL_25_4.service;

import com.example.BTL_25_4.entity.Car;
import java.util.List;
import java.util.Optional;

public interface CarService {
    List<Car> findAllAvailableCars();
    List<Car> searchAvailableCars(String keyword);
    Optional<Car> findCarById(Long id);
}