package com.example.BTL_25_4.entity;

public enum BookingStatus {
    PENDING,        // Chờ xác nhận
    CONFIRMED,      // Đã xác nhận
    IN_PROGRESS,    // Đang thuê
    COMPLETED,      // Đã hoàn thành (trả xe)
    CANCELED        // Đã hủy
}