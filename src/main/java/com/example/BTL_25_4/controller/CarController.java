package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    // Lấy thông tin xe chi tiết
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> carDetails(@PathVariable Long id) {
        Optional<Car> carOptional = carService.findCarById(id);
        if (carOptional.isPresent()) {
            return ResponseEntity.ok(carOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy thông tin xe"));
        }
    }
}