package com.example.BTL_25_4.repository;

import com.example.BTL_25_4.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Tìm kiếm khách hàng theo email
    Optional<Customer> findByEmail(String email);

    // Tìm kiếm gần đúng theo tên
    List<Customer> findByFullNameContainingIgnoreCase(String name);
}