package com.example.BTL_25_4.service.impl;

import com.example.BTL_25_4.dto.BookingDetailsDTO;
import com.example.BTL_25_4.dto.MultiBookingRequestDTO; // MỚI
import com.example.BTL_25_4.dto.BookingResultDTO;     // MỚI
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList; // MỚI
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementierung des BookingService.
 * Enthält die Geschäftslogik für die Verwaltung von Fahrzeugbuchungen.
 */
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository; // Repository để tương tác với bảng bookings
    private final CarRepository carRepository;         // Repository để tương tác với bảng cars
    private final CustomerService customerService;     // Service để quản lý thông tin khách hàng

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              CarRepository carRepository,
                              CustomerService customerService) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.customerService = customerService;
    }

    /**
     * Tạo một đơn đặt xe mới dựa trên dữ liệu yêu cầu.
     * Phương thức này được đánh dấu @Transactional, nghĩa là toàn bộ hoạt động bên trong
     * sẽ được coi là một giao dịch duy nhất. Nếu có lỗi xảy ra, giao dịch sẽ được rollback.
     *
     * @param bookingRequestData Dữ liệu yêu cầu đặt xe, bao gồm carIdRequest, thông tin Customer,
     * startDate, endDate, và notes.
     * @return Đối tượng Booking đã được tạo và lưu vào cơ sở dữ liệu.
     * @throws RuntimeException Nếu có lỗi xảy ra trong quá trình kiểm tra hoặc tạo booking
     * (ví dụ: ngày không hợp lệ, xe không tồn tại, xe không có sẵn,
     * trùng lịch đặt, thông tin khách hàng thiếu).
     */
    @Override
    @Transactional
    public Booking createBooking(Booking bookingRequestData) {
        // Kiểm tra cơ bản về ngày
        if (bookingRequestData.getStartDate() == null || bookingRequestData.getEndDate() == null) {
            throw new RuntimeException("Ngày nhận và ngày trả xe không được để trống.");
        }
        if (bookingRequestData.getStartDate().isAfter(bookingRequestData.getEndDate())) {
            throw new RuntimeException("Ngày nhận xe không được sau ngày trả xe.");
        }
        // Sửa lỗi: Kiểm tra ngày nhận xe không được là ngày trong quá khứ.
        if (bookingRequestData.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Ngày nhận xe không được là ngày trong quá khứ.");
        }
        if (bookingRequestData.getCarIdRequest() == null) {
            throw new RuntimeException("Thiếu thông tin ID xe để đặt.");
        }

        // Lấy thông tin xe từ cơ sở dữ liệu
        Car car = carRepository.findById(bookingRequestData.getCarIdRequest())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + bookingRequestData.getCarIdRequest()));

        // Kiểm tra trạng thái sẵn có chung của xe
        if (!car.isAvailable()) {
            throw new RuntimeException("Xe " + car.getBrand() + " " + car.getModel() + " hiện không có sẵn (trạng thái chung không available).");
        }

        // Kiểm tra xem có lịch đặt nào bị trùng cho xe này và khoảng ngày này không
        // Ngày kết thúc trong câu query cần cộng thêm 1 vì logic của existsConflictingBooking
        // coi reqEndDate là ngày bắt đầu không được phép trùng.
        boolean hasConflict = bookingRepository.existsConflictingBooking(
                car.getId(),
                bookingRequestData.getStartDate(),
                bookingRequestData.getEndDate().plusDays(1)
        );
        if (hasConflict) {
            throw new RuntimeException("Xe " + car.getBrand() + " " + car.getModel() + " đã được đặt trong khoảng thời gian bạn chọn.");
        }

        // Tìm hoặc tạo mới khách hàng
        Customer customerForBooking;
        Customer customerFromRequest = bookingRequestData.getCustomer();

        // Kiểm tra thông tin khách hàng từ yêu cầu
        if (customerFromRequest == null || !StringUtils.hasText(customerFromRequest.getEmail())) {
            throw new RuntimeException("Thông tin email khách hàng không được để trống.");
        }
        if (!StringUtils.hasText(customerFromRequest.getFullName()) || !StringUtils.hasText(customerFromRequest.getPhoneNumber())) {
            throw new RuntimeException("Vui lòng cung cấp đủ Họ tên và Số điện thoại khách hàng.");
        }

        // Sử dụng CustomerService để tìm khách hàng hiện có bằng email hoặc tạo mới nếu chưa có
        customerForBooking = customerService.findOrCreateCustomerForBooking(
                customerFromRequest.getFullName(),
                customerFromRequest.getEmail(),
                customerFromRequest.getPhoneNumber(),
                customerFromRequest.getAddress()
        );

        // Tính tổng số ngày thuê (bao gồm cả ngày bắt đầu và ngày kết thúc)
        long numberOfDays = ChronoUnit.DAYS.between(bookingRequestData.getStartDate(), bookingRequestData.getEndDate()) + 1;
        if (numberOfDays <= 0) { // Số ngày thuê phải lớn hơn 0
            throw new RuntimeException("Số ngày thuê không hợp lệ cho xe " + car.getBrand() + " " + car.getModel() + ".");
        }
        double totalPrice = car.getPricePerDay() * numberOfDays; // Tính tổng giá

        // Tạo đối tượng Booking mới
        Booking newBooking = new Booking();
        newBooking.setCar(car); // Gán xe
        newBooking.setCustomer(customerForBooking); // Gán khách hàng
        newBooking.setStartDate(bookingRequestData.getStartDate()); // Gán ngày nhận
        newBooking.setEndDate(bookingRequestData.getEndDate());     // Gán ngày trả
        newBooking.setTotalPrice(totalPrice); // Gán tổng giá
        newBooking.setStatus("PENDING"); // Trạng thái mặc định khi mới tạo đơn là "PENDING" (Chờ xử lý/xác nhận)
        newBooking.setNotes(bookingRequestData.getNotes()); // Gán ghi chú (nếu có)
        // bookingDate sẽ được tự động gán giá trị thời gian hiện tại nhờ @CreationTimestamp

        return bookingRepository.save(newBooking); // Lưu đơn đặt xe vào cơ sở dữ liệu
    }

    /**
     * Lấy danh sách các khoảng ngày đã được đặt cho một xe cụ thể.
     * Chỉ bao gồm các booking đang hoạt động (status != 'CANCELED' và endDate >= ngày hiện tại).
     * Được đánh dấu @Transactional(readOnly = true) để tối ưu hóa cho các thao tác chỉ đọc.
     *
     * @param carId ID của xe.
     * @return Danh sách các Map, mỗi Map chứa "startDate" và "endDate" dưới dạng chuỗi.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getBookedDateMapsForCar(Long carId) {
        LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại
        // Tìm các booking đang hoạt động cho xe
        List<Booking> bookings = bookingRepository.findActiveBookingsForCar(carId, currentDate);
        // Chuyển đổi danh sách Booking thành danh sách các Map chứa ngày bắt đầu và kết thúc
        return bookings.stream()
                .map(booking -> {
                    Map<String, String> dateMap = new HashMap<>();
                    dateMap.put("startDate", booking.getStartDate().toString());
                    dateMap.put("endDate", booking.getEndDate().toString());
                    return dateMap;
                })
                .collect(Collectors.toList());
    }

    /**
     * Tìm chi tiết một đơn đặt xe cụ thể dựa theo ID và chuyển đổi thành BookingDetailsDTO.
     * Được đánh dấu @Transactional(readOnly = true).
     *
     * @param bookingId ID của đơn đặt xe cần tìm.
     * @return Optional chứa BookingDetailsDTO nếu tìm thấy, ngược lại là Optional rỗng.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<BookingDetailsDTO> findBookingDetailsById(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId); // Tìm booking bằng ID
        if (bookingOpt.isPresent()) { // Nếu tìm thấy booking
            Booking booking = bookingOpt.get();
            Customer customer = booking.getCustomer(); // Lấy thông tin khách hàng liên quan
            Car car = booking.getCar();                 // Lấy thông tin xe liên quan

            // Tạo đối tượng DTO để trả về thông tin chi tiết
            BookingDetailsDTO dto = new BookingDetailsDTO(
                    booking.getId(),
                    customer.getFullName() ,
                    customer.getEmail(),
                    customer.getPhoneNumber(),
                    car.getBrand(),
                    car.getModel(),
                    car.getLicensePlate(),
                    booking.getStartDate(),
                    booking.getEndDate(),
                    booking.getTotalPrice(),
                    booking.getStatus(),
                    booking.getNotes(),
                    booking.getBookingDate()
            );
            return Optional.of(dto); // Trả về DTO trong Optional
        }
        return Optional.empty(); // Trả về Optional rỗng nếu không tìm thấy
    }

    /**
     * Xử lý yêu cầu đặt nhiều xe cùng một lúc cho cùng một khách hàng và cùng khoảng thời gian.
     * Mỗi xe trong danh sách sẽ được thử đặt riêng lẻ.
     *
     * Lưu ý về Transaction:
     * - Phương thức `createBooking` được gọi bên trong vòng lặp đã được đánh dấu `@Transactional`.
     * Điều này có nghĩa là mỗi lần gọi `createBooking` sẽ chạy trong một giao dịch riêng.
     * - Nếu phương thức `createMultipleBookings` này cũng được đánh dấu `@Transactional` (như hiện tại),
     * và nếu một `createBooking` con ném ra một RuntimeException không được bắt,
     * thì giao dịch cha (của `createMultipleBookings`) có thể bị rollback, tùy thuộc vào cấu hình
     * và loại Exception.
     * - Để đảm bảo mỗi booking được xử lý độc lập (một booking lỗi không ảnh hưởng booking khác),
     * cần đảm bảo `createBooking` có `Propagation.REQUIRES_NEW` nếu `createMultipleBookings`
     * cũng là `@Transactional`, hoặc `createMultipleBookings` không nên là `@Transactional`
     * nếu muốn dựa vào transaction của từng `createBooking`.
     * - Hiện tại, với `createBooking` là `@Transactional` (mặc định là `Propagation.REQUIRED`),
     * nếu `createMultipleBookings` cũng `@Transactional`, chúng sẽ chia sẻ cùng một giao dịch cha.
     * Một lỗi RuntimeException không được xử lý trong `createBooking` sẽ làm rollback toàn bộ.
     * Tuy nhiên, do chúng ta bắt `RuntimeException` trong vòng lặp và thêm vào `results`,
     * giao dịch cha sẽ không bị rollback bởi các lỗi đó, cho phép các booking khác tiếp tục.
     *
     * @param multiBookingRequest DTO chứa thông tin khách hàng, ngày đặt chung, danh sách ID xe, và ghi chú chung.
     * @return Danh sách các {@link BookingResultDTO}, mỗi DTO chứa kết quả của việc đặt một xe.
     */
    @Override
    @Transactional // Xem xét kỹ lưỡng về quản lý transaction cho kịch bản đặt nhiều xe.
    public List<BookingResultDTO> createMultipleBookings(MultiBookingRequestDTO multiBookingRequest) {
        List<BookingResultDTO> results = new ArrayList<>(); // Danh sách lưu kết quả cho từng xe

        // --- Bước 1: Kiểm tra dữ liệu chung của yêu cầu ---
        // Kiểm tra ngày nhận và ngày trả không được null
        if (multiBookingRequest.getStartDate() == null || multiBookingRequest.getEndDate() == null) {
            // Nếu ngày không hợp lệ, không thể tiếp tục. Tạo kết quả lỗi cho tất cả các xe.
            multiBookingRequest.getCarIdsToBook().forEach(carId -> {
                results.add(new BookingResultDTO(carId, null, null, false, "Ngày nhận hoặc ngày trả không hợp lệ.", null, null, multiBookingRequest.getStartDate(), multiBookingRequest.getEndDate()));
            });
            return results; // Trả về kết quả lỗi
        }
        // Ngày nhận không được sau ngày trả
        if (multiBookingRequest.getStartDate().isAfter(multiBookingRequest.getEndDate())) {
            multiBookingRequest.getCarIdsToBook().forEach(carId -> {
                results.add(new BookingResultDTO(carId, null, null, false, "Ngày nhận không được sau ngày trả.", null, null, multiBookingRequest.getStartDate(), multiBookingRequest.getEndDate()));
            });
            return results;
        }
        // Ngày nhận không được là ngày trong quá khứ
        if (multiBookingRequest.getStartDate().isBefore(LocalDate.now())) {
            multiBookingRequest.getCarIdsToBook().forEach(carId -> {
                results.add(new BookingResultDTO(carId, null, null, false, "Ngày nhận không được là ngày trong quá khứ.", null, null, multiBookingRequest.getStartDate(), multiBookingRequest.getEndDate()));
            });
            return results;
        }

        // --- Bước 2: Xử lý thông tin khách hàng ---
        // Lấy thông tin khách hàng từ yêu cầu
        Customer customerFromRequest = multiBookingRequest.getCustomer();
        // Kiểm tra thông tin khách hàng cơ bản
        if (customerFromRequest == null || !StringUtils.hasText(customerFromRequest.getEmail()) ||
                !StringUtils.hasText(customerFromRequest.getFullName()) || !StringUtils.hasText(customerFromRequest.getPhoneNumber())) {
            // Nếu thông tin khách hàng không đủ, tạo kết quả lỗi cho tất cả các xe
            multiBookingRequest.getCarIdsToBook().forEach(carId -> {
                results.add(new BookingResultDTO(carId, null, null, false, "Thông tin khách hàng không đầy đủ (Họ tên, Email, SĐT).", null, null, multiBookingRequest.getStartDate(), multiBookingRequest.getEndDate()));
            });
            return results; // Trả về kết quả lỗi
        }

        // Tìm hoặc tạo mới khách hàng một lần cho tất cả các booking trong yêu cầu này
        Customer sharedCustomer = customerService.findOrCreateCustomerForBooking(
                customerFromRequest.getFullName(),
                customerFromRequest.getEmail(),
                customerFromRequest.getPhoneNumber(),
                customerFromRequest.getAddress()
        );

        // --- Bước 3: Xử lý đặt từng xe ---
        // Lặp qua danh sách ID các xe muốn đặt
        for (Long carId : multiBookingRequest.getCarIdsToBook()) {
            // Tìm xe trong cơ sở dữ liệu bằng ID
            Car car = carRepository.findById(carId).orElse(null);
            if (car == null) { // Nếu không tìm thấy xe
                // Thêm kết quả lỗi vào danh sách
                results.add(new BookingResultDTO(carId, "N/A", "N/A", false, "Không tìm thấy xe với ID: " + carId, null, null, multiBookingRequest.getStartDate(), multiBookingRequest.getEndDate()));
                continue; // Chuyển sang xe tiếp theo
            }

            // Tạo một đối tượng Booking tạm thời để truyền vào phương thức createBooking
            // Đối tượng này sẽ chứa thông tin chung (khách hàng, ngày, ghi chú) và ID của xe hiện tại
            Booking bookingRequestDataForEachCar = new Booking();
            bookingRequestDataForEachCar.setCarIdRequest(carId); // ID của xe cụ thể này
            bookingRequestDataForEachCar.setCustomer(sharedCustomer); // Khách hàng chung đã được xử lý
            bookingRequestDataForEachCar.setStartDate(multiBookingRequest.getStartDate()); // Ngày nhận chung
            bookingRequestDataForEachCar.setEndDate(multiBookingRequest.getEndDate());     // Ngày trả chung
            bookingRequestDataForEachCar.setNotes(multiBookingRequest.getNotes());       // Ghi chú chung

            try {
                // Gọi lại phương thức createBooking (đã xử lý logic đặt một xe)
                // Phương thức này sẽ ném RuntimeException nếu có lỗi (ví dụ: trùng lịch, xe không available)
                Booking createdBooking = this.createBooking(bookingRequestDataForEachCar);
                // Nếu không có lỗi, đặt xe thành công
                results.add(new BookingResultDTO(
                        carId, car.getBrand(), car.getModel(), // Thông tin xe
                        true, "Đặt xe thành công!",           // Trạng thái và tin nhắn
                        createdBooking.getId(), createdBooking.getTotalPrice(), // ID booking và giá
                        createdBooking.getStartDate(), createdBooking.getEndDate() // Ngày thực tế (phòng trường hợp có thay đổi)
                ));
            } catch (RuntimeException e) { // Nếu có lỗi trong quá trình đặt xe này (ví dụ: createBooking ném lỗi)
                // Thêm kết quả thất bại vào danh sách, kèm theo thông báo lỗi
                results.add(new BookingResultDTO(
                        carId, car.getBrand(), car.getModel(),
                        false, e.getMessage(), // Lấy tin nhắn lỗi từ exception
                        null, null,
                        multiBookingRequest.getStartDate(), multiBookingRequest.getEndDate()
                ));
            }
        }
        return results; // Trả về danh sách kết quả cho từng xe
    }
}