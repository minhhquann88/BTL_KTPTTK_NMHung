package com.example.BTL_25_4.service;

import com.example.BTL_25_4.entity.Booking;

import java.util.List;
import java.util.Map;

public interface BookingService {
    Booking createBooking(Booking bookingRequestData);

    List<Map<String, String>> getBookedDateMapsForCar(Long carId);
}