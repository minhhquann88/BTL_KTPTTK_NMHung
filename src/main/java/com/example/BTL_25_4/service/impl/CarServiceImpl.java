package com.example.BTL_25_4.service.impl;

import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.repository.CarRepository;
import com.example.BTL_25_4.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Car> findAllAvailableCars() {
        return carRepository.findByAvailable(true);
    }
    @Override
    @Transactional(readOnly = true)
    public List<Car> searchAvailableCars(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllAvailableCars();
        }
        return carRepository.searchAvailableCars(keyword.trim());
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<Car> findCarById(Long id) {
        return carRepository.findById(id);
    }
}