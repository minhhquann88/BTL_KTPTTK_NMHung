package com.example.BTL_25_4.service.impl;

import com.example.BTL_25_4.dto.CarDTO;
import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.repository.CarRepository;
import com.example.BTL_25_4.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    @Transactional(readOnly = true) // Giao dịch chỉ đọc, tối ưu hơn
    public List<CarDTO> findAllAvailableCars() {
        return carRepository.findByAvailable(true).stream()
                .map(this::mapToCarDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDTO> searchAvailableCars(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllAvailableCars();
        }
        // Sử dụng query đã tạo trong Repository
        return carRepository.searchAvailableCars(keyword.trim()).stream()
                .map(this::mapToCarDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarDTO> findCarById(Long id) {
        return carRepository.findById(id).map(this::mapToCarDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDTO> findAvailableCarsForPeriod(LocalDate startDate, LocalDate endDate) {
        // Sử dụng query đã tạo trong Repository
        return carRepository.findAvailableCarsForPeriod(startDate, endDate).stream()
                .map(this::mapToCarDTO)
                .collect(Collectors.toList());
    }

    // --- Helper method for mapping ---
    private CarDTO mapToCarDTO(Car car) {
        return new CarDTO(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getLicensePlate(),
                car.getProductionYear(),
                car.getPricePerDay(),
                car.getDescription(),
                car.getImageUrl(),
                car.isAvailable()
        );
    }
}