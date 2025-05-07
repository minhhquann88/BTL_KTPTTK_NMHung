package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.dto.CarDTO;
import com.example.BTL_25_4.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final CarService carService;

    @Autowired
    public HomeController(CarService carService) {
        this.carService = carService;
    }

    // Trang chủ hiển thị danh sách xe có sẵn
    @GetMapping("/")
    public String homePage(Model model) {
        List<CarDTO> availableCars = carService.findAllAvailableCars();
        model.addAttribute("cars", availableCars);
        model.addAttribute("pageTitle", "Danh sách xe có sẵn");
        return "home"; // Trả về templates/home.html
    }

    // Tìm kiếm xe
    @GetMapping("/search")
    public String searchCars(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<CarDTO> searchResult = carService.searchAvailableCars(keyword);
        model.addAttribute("cars", searchResult);
        model.addAttribute("pageTitle", "Kết quả tìm kiếm");
        model.addAttribute("keyword", keyword);
        return "home"; // Sử dụng lại template home
    }
}