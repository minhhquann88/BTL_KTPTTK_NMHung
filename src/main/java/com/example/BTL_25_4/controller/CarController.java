package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.dto.CarDTO;
import com.example.BTL_25_4.exception.ResourceNotFoundException;
import com.example.BTL_25_4.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/cars") // Nhóm các URL liên quan đến xe
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    // Xem chi tiết xe
    @GetMapping("/{id}")
    public String carDetails(@PathVariable Long id, Model model) {
        Optional<CarDTO> carOptional = carService.findCarById(id);

        if (carOptional.isPresent()) {
            model.addAttribute("car", carOptional.get());
            model.addAttribute("pageTitle", "Chi tiết xe - " + carOptional.get().getBrand() + " " + carOptional.get().getModel());
            return "cars/details"; // Trả về templates/cars/details.html
        } else {
            // Ném exception để GlobalExceptionHandler xử lý và trả về trang 404
            throw new ResourceNotFoundException("Không tìm thấy xe với ID: " + id);
            // Hoặc trả về trang lỗi cụ thể nếu không dùng GlobalExceptionHandler
            // return "error/404";
        }
    }
}