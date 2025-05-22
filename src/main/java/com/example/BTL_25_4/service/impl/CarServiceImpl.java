package com.example.BTL_25_4.service.impl;

import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.repository.BookingRepository;
import com.example.BTL_25_4.repository.CarRepository;
import com.example.BTL_25_4.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, BookingRepository bookingRepository) {
        this.carRepository = carRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> findAllAvailableCars() {
        return carRepository.findByAvailable(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Car> findCarById(Long id) {
        return carRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> findAvailableCarsByDateRange(LocalDate userStartDate, LocalDate userEndDate) {
        if (userStartDate == null || userEndDate == null || userStartDate.isAfter(userEndDate)) {
            return Collections.emptyList();
        }

        // Lấy danh sách ID các xe có booking CHỒNG LẤN với khoảng thời gian người dùng yêu cầu
        List<Long> bookedCarIds = bookingRepository.findBookedCarIdsOverlappingWithDateRange(userStartDate, userEndDate);

        List<Car> allGenerallyAvailableCars = carRepository.findByAvailable(true);

        if (bookedCarIds.isEmpty()) {
            return allGenerallyAvailableCars;
        } else {
            // Loại bỏ những xe đã có lịch đặt chồng lấn
            return allGenerallyAvailableCars.stream()
                    .filter(car -> !bookedCarIds.contains(car.getId()))
                    .collect(Collectors.toList());
        }
    }
}