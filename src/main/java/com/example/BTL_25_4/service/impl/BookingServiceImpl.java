package com.example.BTL_25_4.service.impl;

import com.example.BTL_25_4.entity.Booking;
import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.entity.Customer;
import com.example.BTL_25_4.repository.BookingRepository;
import com.example.BTL_25_4.repository.CarRepository;
import com.example.BTL_25_4.service.BookingService;
import com.example.BTL_25_4.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import java.time.temporal.ChronoUnit;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final CustomerService customerService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              CarRepository carRepository,
                              CustomerService customerService) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.customerService = customerService;
    }

    @Override
    @Transactional
    public Booking createBooking(Booking bookingRequestData) {
        if (bookingRequestData.getStartDate() == null || bookingRequestData.getEndDate() == null) {
            throw new RuntimeException("BAD_REQUEST:Ngày nhận và ngày trả xe không được để trống.");
        }
        if (bookingRequestData.getStartDate().isAfter(bookingRequestData.getEndDate())) {
            throw new RuntimeException("BAD_REQUEST:Ngày nhận xe không được sau ngày trả xe.");
        }
        if (bookingRequestData.getCarIdRequest() == null) {
            throw new RuntimeException("BAD_REQUEST:Thiếu thông tin ID xe để đặt.");
        }

        Car car = carRepository.findById(bookingRequestData.getCarIdRequest())
                .orElseThrow(() -> {
                    return new RuntimeException("NOT_FOUND:Không tìm thấy xe với ID: " + bookingRequestData.getCarIdRequest());
                });

        if (!car.isAvailable()) {
            throw new RuntimeException("CONFLICT:Xe ID " + car.getId() + " hiện không có sẵn để đặt.");
        }

        boolean hasConflict = bookingRepository.existsConflictingBooking(
                car.getId(),
                bookingRequestData.getStartDate(),
                bookingRequestData.getEndDate().plusDays(1)
        );
        if (hasConflict) {
            throw new RuntimeException("CONFLICT:Xe này đã được đặt trong khoảng thời gian bạn chọn. Vui lòng chọn thời gian khác.");
        }

        Customer customerForBooking;
        Customer customerFromRequest = bookingRequestData.getCustomer();

        if (customerFromRequest == null || !StringUtils.hasText(customerFromRequest.getEmail())) {
            throw new RuntimeException("BAD_REQUEST:Thông tin email khách hàng không được để trống.");
        }
        if (!StringUtils.hasText(customerFromRequest.getFullName()) || !StringUtils.hasText(customerFromRequest.getPhoneNumber())) {
            throw new RuntimeException("BAD_REQUEST:Vui lòng cung cấp đủ Họ tên và Số điện thoại khách hàng.");
        }

        customerForBooking = customerService.findOrCreateCustomerForBooking(
                customerFromRequest.getFullName(),
                customerFromRequest.getEmail(),
                customerFromRequest.getPhoneNumber(),
                customerFromRequest.getAddress()
        );

        long numberOfDays = ChronoUnit.DAYS.between(bookingRequestData.getStartDate(), bookingRequestData.getEndDate()) + 1;
        if (numberOfDays <= 0) {
            throw new RuntimeException("BAD_REQUEST:Số ngày thuê phải lớn hơn 0.");
        }
        double totalPrice = car.getPricePerDay() * numberOfDays;

        Booking newBooking = new Booking();
        newBooking.setCar(car);
        newBooking.setCustomer(customerForBooking);
        newBooking.setStartDate(bookingRequestData.getStartDate());
        newBooking.setEndDate(bookingRequestData.getEndDate());
        newBooking.setTotalPrice(totalPrice);
        newBooking.setStatus("PENDING");
        newBooking.setNotes(bookingRequestData.getNotes());

        return bookingRepository.save(newBooking);
    }
    // Phương thức được sửa đổi
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getBookedDateMapsForCar(Long carId) {
        LocalDate currentDate = LocalDate.now();
        List<Booking> bookings = bookingRepository.findActiveBookingsForCar(carId, currentDate);
        return bookings.stream()
                .map(booking -> {
                    Map<String, String> dateMap = new HashMap<>();
                    dateMap.put("startDate", booking.getStartDate().toString()); // Chuyển LocalDate thành "YYYY-MM-DD"
                    dateMap.put("endDate", booking.getEndDate().toString());   // Chuyển LocalDate thành "YYYY-MM-DD"
                    return dateMap;
                })
                .collect(Collectors.toList());
    }
}