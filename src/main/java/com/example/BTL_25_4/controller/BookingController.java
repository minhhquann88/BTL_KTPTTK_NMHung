package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.dto.BookingDetailsDTO;
import com.example.BTL_25_4.dto.MultiBookingRequestDTO; // MỚI
import com.example.BTL_25_4.dto.BookingResultDTO;     // MỚI
import com.example.BTL_25_4.entity.Booking;
import com.example.BTL_25_4.entity.Car;
import com.example.BTL_25_4.service.BookingService;
import com.example.BTL_25_4.service.CarService;
import jakarta.validation.Valid; // MỚI: Để kích hoạt validation cho DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors; // MỚI

/**
 * Controller quản lý các API liên quan đến việc đặt xe.
 */
@RestController
@RequestMapping("/api/bookings") // Tất cả các API trong controller này sẽ có tiền tố /api/bookings
public class BookingController {
    private final BookingService bookingService; // Service xử lý logic đặt xe
    private final CarService carService;         // Service xử lý logic liên quan đến xe

    @Autowired
    public BookingController(BookingService bookingService, CarService carService) {
        this.bookingService = bookingService;
        this.carService = carService;
    }

    /**
     * API lấy thông tin xe cơ bản cho trang đặt xe.
     * Được sử dụng để hiển thị thông tin xe khi người dùng chọn một xe để đặt.
     * @param carId ID của xe.
     * @return ResponseEntity chứa thông tin xe nếu tìm thấy, hoặc lỗi 404 nếu không tìm thấy.
     */
    @GetMapping("/car-info-for-booking/{carId}")
    public ResponseEntity<?> getCarInfoForBookingPage(@PathVariable Long carId) {
        Optional<Car> carOptional = carService.findCarById(carId);
        if (carOptional.isPresent()) {
            return ResponseEntity.ok(carOptional.get()); // Trả về thông tin xe với HTTP 200 OK
        } else {
            // Trả về lỗi 404 Not Found nếu không tìm thấy xe
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy thông tin xe với ID: " + carId));
        }
    }

    /**
     * API xử lý yêu cầu đặt một xe (đặt đơn lẻ).
     * Nhận dữ liệu đặt xe từ request body.
     * @param bookingRequest Đối tượng Booking chứa thông tin đặt xe. @Valid để kiểm tra các ràng buộc.
     * @return ResponseEntity chứa đơn đặt xe đã tạo nếu thành công (HTTP 201 Created),
     * hoặc thông báo lỗi nếu thất bại (HTTP 400 Bad Request).
     */
    @PostMapping // Endpoint cho đặt một xe
    public ResponseEntity<?> processBooking(@Valid @RequestBody Booking bookingRequest) {
        try {
            // Gọi service để tạo booking
            Booking createdBooking = bookingService.createBooking(bookingRequest);
            // Trả về booking đã tạo với HTTP 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (RuntimeException e) {
            // Nếu có lỗi (ví dụ: xe không có sẵn, ngày không hợp lệ), trả về lỗi HTTP 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * API MỚI: Xử lý yêu cầu đặt nhiều xe cùng một lúc.
     * Nhận một đối tượng MultiBookingRequestDTO chứa thông tin khách hàng,
     * ngày đặt chung và danh sách các ID xe muốn đặt.
     *
     * @param multiBookingRequest Đối tượng DTO chứa yêu cầu đặt nhiều xe.
     * Annotation @Valid sẽ kích hoạt việc kiểm tra các ràng buộc được định nghĩa trong DTO.
     * @return ResponseEntity chứa danh sách các {@link BookingResultDTO}, mỗi DTO biểu diễn kết quả
     * của việc đặt một xe.
     * - HTTP 200 OK: Nếu yêu cầu được xử lý (có thể thành công một phần hoặc toàn bộ).
     * - HTTP 400 Bad Request: Nếu có lỗi chung nghiêm trọng (ví dụ: ngày không hợp lệ cho toàn bộ yêu cầu)
     * hoặc tất cả các nỗ lực đặt xe đều thất bại.
     */
    @PostMapping("/multiple") // Endpoint mới cho đặt nhiều xe
    public ResponseEntity<List<BookingResultDTO>> processMultipleBookings(@Valid @RequestBody MultiBookingRequestDTO multiBookingRequest) {
        // Kiểm tra cơ bản về ngày (có thể được thực hiện bởi validation annotations trong DTO)
        // Ví dụ: Ngày nhận không được sau ngày trả
        if (multiBookingRequest.getStartDate() != null && multiBookingRequest.getEndDate() != null &&
                multiBookingRequest.getStartDate().isAfter(multiBookingRequest.getEndDate())) {
            // Nếu ngày không hợp lệ, tạo danh sách kết quả lỗi cho tất cả các xe
            List<BookingResultDTO> errorResults = multiBookingRequest.getCarIdsToBook().stream()
                    .map(carId -> new BookingResultDTO(carId, null, null, false, "Ngày nhận xe không được sau ngày trả xe.", null, null, multiBookingRequest.getStartDate(), multiBookingRequest.getEndDate()))
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorResults); // Trả về lỗi 400
        }

        // Gọi service để xử lý việc đặt nhiều xe
        List<BookingResultDTO> results = bookingService.createMultipleBookings(multiBookingRequest);

        // Kiểm tra xem tất cả các xe có đặt thất bại không
        boolean allFailed = results.stream().allMatch(r -> !r.isSuccess());
        if (allFailed && !results.isEmpty()) {
            // Nếu tất cả đều thất bại, có thể trả về HTTP 400 Bad Request
            // Hoặc, có thể luôn trả về HTTP 200 OK và để client tự xử lý kết quả chi tiết.
            // Ở đây, nếu tất cả thất bại, trả về 400.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(results);
        }
        // Nếu có ít nhất một xe đặt thành công, hoặc không có xe nào được yêu cầu, trả về HTTP 200 OK
        return ResponseEntity.ok(results);
    }


    /**
     * API lấy danh sách các ngày đã được đặt cho một xe cụ thể.
     * Dùng để hiển thị lịch bận của xe cho người dùng.
     * @param carId ID của xe.
     * @return ResponseEntity chứa danh sách các khoảng ngày đã đặt.
     */
    @GetMapping("/car/{carId}/booked-dates")
    public ResponseEntity<List<Map<String, String>>> getBookedDatesForCar(@PathVariable Long carId) {
        List<Map<String, String>> bookedDateMaps = bookingService.getBookedDateMapsForCar(carId);
        return ResponseEntity.ok(bookedDateMaps);
    }

    /**
     * API lấy chi tiết một đơn đặt xe cụ thể (dùng cho trang hóa đơn/xác nhận).
     * @param bookingId ID của đơn đặt xe.
     * @return ResponseEntity chứa BookingDetailsDTO nếu tìm thấy, hoặc lỗi 404 nếu không.
     */
    @GetMapping("/details/{bookingId}")
    public ResponseEntity<?> getBookingDetails(@PathVariable Long bookingId) {
        Optional<BookingDetailsDTO> bookingDetailsDTOOptional = bookingService.findBookingDetailsById(bookingId);
        if (bookingDetailsDTOOptional.isPresent()) {
            return ResponseEntity.ok(bookingDetailsDTOOptional.get()); // Trả về chi tiết với HTTP 200 OK
        } else {
            // Trả về lỗi 404 Not Found nếu không tìm thấy đơn đặt xe
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy đơn đặt xe với ID: " + bookingId));
        }
    }
}