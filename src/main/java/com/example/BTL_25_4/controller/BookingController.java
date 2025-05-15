package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.entity.Booking;
import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.service.BookingService;
import com.example.BTL_25_4.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final CarService carService;

    @Autowired
    public BookingController(BookingService bookingService, CarService carService) {
        this.bookingService = bookingService;
        this.carService = carService;
    }
    @GetMapping("/car-info/{carId}")
    public ResponseEntity<?> getCarInfoForBooking(@PathVariable Long carId) {
        Optional<Car> carOptional = carService.findCarById(carId);
        if (carOptional.isPresent()) {
            return ResponseEntity.ok(carOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy thông tin xe với ID: " + carId));
        }
    }
    @PostMapping
    public ResponseEntity<Booking> processBooking(@RequestBody Booking bookingRequest) {
        Booking createdBooking = bookingService.createBooking(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    // Endpoint được sửa đổi kiểu trả về
    @GetMapping("/car/{carId}/booked-dates")
    public ResponseEntity<List<Map<String, String>>> getBookedDatesForCar(@PathVariable Long carId) {
        List<Map<String, String>> bookedDateMaps = bookingService.getBookedDateMapsForCar(carId);
        return ResponseEntity.ok(bookedDateMaps); // Trả về danh sách rỗng nếu không có
    }
}