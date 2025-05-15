package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    // Tìm kiếm xe dựa trên một từ khóa
    @GetMapping("/search")
    public List<Car> searchCars(@RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return carService.findAllAvailableCars();
        } else {
            return carService.searchAvailableCars(keyword);
        }
    }
}