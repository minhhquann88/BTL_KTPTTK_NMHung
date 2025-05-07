package com.example.BTL_25_4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "Trang Quản Trị");
        return "admin/dashboard"; // Trả về templates/admin/dashboard.html
    }
}