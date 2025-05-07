package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.dto.BookingDTO;
import com.example.BTL_25_4.dto.BookingRequestDTO;
import com.example.BTL_25_4.dto.CarDTO;
import com.example.BTL_25_4.exception.CarNotAvailableException;
import com.example.BTL_25_4.exception.ResourceNotFoundException;
import com.example.BTL_25_4.exception.ValidationException;
import com.example.BTL_25_4.service.BookingService;
import com.example.BTL_25_4.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CarService carService;

    @Autowired
    public BookingController(BookingService bookingService, CarService carService) {
        this.bookingService = bookingService;
        this.carService = carService;
    }

    // Hiển thị form tạo booking
    @GetMapping("/create/{carId}")
    public String showBookingForm(@PathVariable Long carId, Model model) {
        Optional<CarDTO> carOptional = carService.findCarById(carId);
        if (carOptional.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId);
        }
        model.addAttribute("car", carOptional.get());
        model.addAttribute("bookingRequest", new BookingRequestDTO());
        model.addAttribute("pageTitle", "Đặt xe: " + carOptional.get().getBrand() + " " + carOptional.get().getModel());
        return "bookings/create";
    }

    // Xử lý việc tạo booking
    @PostMapping("/create")
    public String processBooking(
            @Valid @ModelAttribute("bookingRequest") BookingRequestDTO bookingRequest, // DTO chứa cả carId và customer info
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // 1. Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            Optional<CarDTO> carOptional = carService.findCarById(bookingRequest.getCarId());
            if (carOptional.isPresent()) {
                model.addAttribute("car", carOptional.get());
                model.addAttribute("pageTitle", "Đặt xe: " + carOptional.get().getBrand() + " " + carOptional.get().getModel());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Xe bạn đang đặt không còn tồn tại.");
                return "redirect:/";
            }
            model.addAttribute("bookingRequest", bookingRequest);
            return "bookings/create";
        }

        // 2. Gọi service để tạo booking (Chỉ cần truyền requestDTO)
        try {
            // bookingService giờ chỉ cần requestDTO vì nó chứa cả thông tin khách hàng
            BookingDTO createdBooking = bookingService.createBooking(bookingRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt xe thành công! Mã đặt xe của bạn là #" + createdBooking.getId());
            // Chuyển hướng về trang chủ
            return "redirect:/";

        } catch (ResourceNotFoundException | CarNotAvailableException | ValidationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bookings/create/" + bookingRequest.getCarId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình đặt xe. Vui lòng thử lại.");
            return "redirect:/bookings/create/" + bookingRequest.getCarId();
        }
    }
}