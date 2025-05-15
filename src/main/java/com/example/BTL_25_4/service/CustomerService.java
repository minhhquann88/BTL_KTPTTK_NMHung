package com.example.BTL_25_4.service;

import com.example.BTL_25_4.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAllCustomers();
    Optional<Customer> findCustomerById(Long id);
    Customer saveCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customerDetails);
    void deleteCustomer(Long id);
    List<Customer> searchCustomers(String keyword);
    Customer findOrCreateCustomerForBooking(String name, String email, String phone, String address);
}