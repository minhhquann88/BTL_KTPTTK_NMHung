package com.example.BTL_25_4.service.impl;

import com.example.BTL_25_4.dto.BookingDTO;
import com.example.BTL_25_4.dto.BookingRequestDTO;
import com.example.BTL_25_4.entity.Booking;
import com.example.BTL_25_4.entity.BookingStatus;
import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.entity.Customer; // Import Customer
import com.example.BTL_25_4.exception.CarNotAvailableException;
import com.example.BTL_25_4.exception.ResourceNotFoundException;
import com.example.BTL_25_4.exception.ValidationException;
import com.example.BTL_25_4.repository.BookingRepository;
import com.example.BTL_25_4.service.CustomerService; // Import CustomerService
import com.example.BTL_25_4.repository.CarRepository;
import com.example.BTL_25_4.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final CustomerService customerService; // <<< THÊM CustomerService

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, CarRepository carRepository, CustomerService customerService) { // Bỏ CustomerRepository
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.customerService = customerService;
    }

    @Override
    @Transactional
    public BookingDTO createBooking(BookingRequestDTO requestDTO) // Bỏ customerId
            throws ResourceNotFoundException, CarNotAvailableException, ValidationException {

        // 1. Validate input dates (Giữ nguyên)
        if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new ValidationException("Ngày bắt đầu không thể sau ngày kết thúc.");
        }
        // Bỏ kiểm tra ngày bắt đầu < ngày hiện tại vì đã có @FutureOrPresent
        // if (requestDTO.getStartDate().isBefore(LocalDate.now())) {
        //     throw new ValidationException("Ngày bắt đầu thuê không được là ngày trong quá khứ.");
        // }

        // 2. Find Car (Bỏ tìm Customer)
        Car car = carRepository.findById(requestDTO.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + requestDTO.getCarId()));

        // 3. Check Car Availability Status (Giữ nguyên)
        if (!car.isAvailable()) {
            throw new CarNotAvailableException("Xe " + car.getBrand() + " " + car.getModel() + " hiện không sẵn sàng cho thuê.");
        }

        // 4. Check for Conflicting Bookings (Giữ nguyên)
        boolean hasConflict = bookingRepository.existsConflictingBooking(
                requestDTO.getCarId(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate()
        );
        if (hasConflict) {
            throw new CarNotAvailableException("Xe đã được đặt trong khoảng thời gian bạn chọn.");
        }

        // 5. Calculate total price (Giữ nguyên)
        long numberOfDays = ChronoUnit.DAYS.between(requestDTO.getStartDate(), requestDTO.getEndDate()) + 1;
        if (numberOfDays <= 0) {
            // Điều này thực tế khó xảy ra nếu validation ngày hoạt động đúng, nhưng vẫn nên có
            throw new ValidationException("Số ngày thuê không hợp lệ.");
        }
        double totalPrice = car.getPricePerDay() * numberOfDays;

        // 6. Find and Validate Customer (Sử dụng phương thức mới)
        //    Phương thức này sẽ ném ValidationException nếu email/sđt không hợp lệ
        Customer customer = customerService.findAndValidateCustomer(
                requestDTO.getCustomerName(),
                requestDTO.getCustomerEmail(),
                requestDTO.getCustomerPhone(),
                requestDTO.getCustomerAddress()
        );
        // Nếu không có exception, customer là hợp lệ (hoặc vừa được tạo)

        // 7. Create and Save Booking (Liên kết với Customer)
        Booking booking = new Booking();
        booking.setCar(car);
        booking.setCustomer(customer); // <<< Liên kết với Customer tìm/tạo được
        booking.setStartDate(requestDTO.getStartDate());
        booking.setEndDate(requestDTO.getEndDate());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING); // Hoặc CONFIRMED

        Booking savedBooking = bookingRepository.save(booking);
        // --- HẾT THAY ĐỔI LOGIC ---


        // 8. Map and return DTO (Cập nhật hàm map)
        return mapToBookingDTO(savedBooking);
    }

    // XÓA phương thức getBookingHistory(...)

    @Override
    @Transactional(readOnly = true)
    public Optional<BookingDTO> findBookingById(Long bookingId) { // Giữ lại
        return bookingRepository.findById(bookingId).map(this::mapToBookingDTO);
    }

    // --- CẬP NHẬT Helper method for mapping (Giữ nguyên) ---
    private BookingDTO mapToBookingDTO(Booking booking) {
        Customer customer = booking.getCustomer();
        Long customerId = (customer != null) ? customer.getId() : null;
        String customerName = (customer != null) ? customer.getFullName() : "N/A";
        String customerEmail = (customer != null) ? customer.getEmail() : "N/A";
        String customerPhone = (customer != null) ? customer.getPhoneNumber() : "N/A";

        return new BookingDTO(
                booking.getId(),
                booking.getBookingDate(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCar().getId(),
                booking.getCar().getBrand() + " " + booking.getCar().getModel(),
                booking.getCar().getLicensePlate(),
                customerId,
                customerName,
                customerEmail,
                customerPhone
        );
    }
}