package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.entity.Customer;
import com.example.BTL_25_4.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; // Giữ lại các annotation cần thiết

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/customers")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Lấy danh sách khách hàng (có tìm kiếm)
    @GetMapping
    public List<Customer> listCustomers(@RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return customerService.searchCustomers(keyword);
        } else {
            return customerService.findAllCustomers();
        }
    }

    // Lấy khách hàng theo ID
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        Optional<Customer> customerOptional = customerService.findCustomerById(id);
        return customerOptional.orElse(null);
    }

    // --- Tạo mới khách hàng ---
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        customer.setId(null);
        return customerService.saveCustomer(customer);
    }

    // Cập nhật khách hàng theo ID
    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        return customerService.updateCustomer(id, customerDetails);
    }

    // Xóa khách hàng theo ID
    @DeleteMapping("/{id}")
    public Map<String, String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Map.of("message", "Yêu cầu xóa khách hàng đã được thực hiện.");
    }
}