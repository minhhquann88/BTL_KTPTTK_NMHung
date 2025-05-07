package com.example.BTL_25_4.service;

import com.example.BTL_25_4.dto.CustomerDTO; // Dùng lại CustomerDTO hoặc tạo DTO riêng nếu cần
import com.example.BTL_25_4.entity.Customer; // Import Customer entity
import com.example.BTL_25_4.exception.ResourceNotFoundException;
import com.example.BTL_25_4.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    // ... (Các phương thức cũ giữ nguyên) ...
    List<CustomerDTO> findAllCustomers();
    Optional<CustomerDTO> findCustomerById(Long id);
    CustomerDTO saveCustomer(CustomerDTO customerDTO) throws ValidationException;
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws ResourceNotFoundException, ValidationException;
    void deleteCustomer(Long id) throws ResourceNotFoundException;
    List<CustomerDTO> searchCustomers(String keyword);

    // --- CẬP NHẬT PHƯƠNG THỨC NÀY ---
    /**
     * Tìm khách hàng bằng email và số điện thoại chính xác.
     * Nếu tìm thấy, trả về khách hàng đó.
     * Nếu không tìm thấy cặp email-sđt này, nhưng email hoặc sđt đã tồn tại với cặp khác, ném ValidationException.
     * Nếu cả email và sđt đều chưa tồn tại trong hệ thống, tạo khách hàng mới.
     * @param name Tên khách hàng
     * @param email Email khách hàng
     * @param phone Số điện thoại khách hàng
     * @param address Địa chỉ khách hàng
     * @return Customer entity đã tồn tại hoặc vừa được tạo.
     * @throws ValidationException Nếu email hoặc số điện thoại đã tồn tại nhưng không khớp với cặp được cung cấp.
     */
    Customer findAndValidateCustomer(String name, String email, String phone, String address) throws ValidationException; // Đổi tên và thêm throws
    // --- HẾT PHẦN CẬP NHẬT ---
}