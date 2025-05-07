package com.example.BTL_25_4.repository;

import com.example.BTL_25_4.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Tìm kiếm khách hàng theo email (hữu ích để kiểm tra trùng lặp)
    Optional<Customer> findByEmail(String email);

    // Tìm kiếm khách hàng theo số điện thoại
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // THÊM MỚI: Tìm khách hàng theo cả email VÀ số điện thoại
    Optional<Customer> findByEmailAndPhoneNumber(String email, String phoneNumber);

    // Tìm kiếm gần đúng theo tên (không phân biệt hoa thường)
    List<Customer> findByFullNameContainingIgnoreCase(String name);

    // Tìm kiếm theo email hoặc sđt hoặc tên
    // Ví dụ sử dụng JPQL Query:
    // @Query("SELECT c FROM Customer c WHERE LOWER(c.fullName) LIKE LOWER(concat('%', :keyword, '%')) OR c.email LIKE %:keyword% OR c.phoneNumber LIKE %:keyword%")
    // List<Customer> searchCustomers(@Param("keyword") String keyword);

    // Hoặc dùng Specification nếu cần tìm kiếm phức tạp hơn
}