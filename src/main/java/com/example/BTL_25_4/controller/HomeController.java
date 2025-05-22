package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class HomeController {

    private final CarService carService;

    @Autowired
    public HomeController(CarService carService) {
        this.carService = carService;
    }

    // Trả về danh sách tất cả các xe đang sẵn có.
    @GetMapping("/available")
    public List<Car> getAvailableCars() {
        return carService.findAllAvailableCars();
    }

    //  tìm xe trống theo khoảng ngày
    @GetMapping("/available-by-date")
    public List<Car> getAvailableCarsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return carService.findAvailableCarsByDateRange(startDate, endDate);
    }
}