package com.example.BTL_25_4.service.impl;

import com.example.BTL_25_4.entity.Customer;
import com.example.BTL_25_4.repository.CustomerRepository;
import com.example.BTL_25_4.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        if (customer.getId() == null) { // Khi tạo mới
            customerRepository.findByEmail(customer.getEmail()).ifPresent(existingCustomer -> {
                throw new RuntimeException("Email đã tồn tại.");
            });
        }
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Kiểm tra nếu email thay đổi và bị trùng với người khác
        if (!existingCustomer.getEmail().equalsIgnoreCase(customerDetails.getEmail())) {
            customerRepository.findByEmail(customerDetails.getEmail()).ifPresent(conflictCustomer -> {
                throw new RuntimeException("Email đã được sử dụng bởi một khách hàng khác.");
            });
        }
        existingCustomer.setFullName(customerDetails.getFullName());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setPhoneNumber(customerDetails.getPhoneNumber());
        existingCustomer.setAddress(customerDetails.getAddress());

        return customerRepository.save(existingCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khách hàng để xóa.");
        }
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllCustomers();
        }
        return customerRepository.findByFullNameContainingIgnoreCase(keyword.trim());
    }

    @Override
    @Transactional
    public Customer findOrCreateCustomerForBooking(String name, String email, String phone, String address) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email khách hàng không được để trống khi đặt xe.");
        }
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        // ktra email vua dien da ton tai chua
        // roi thi update
        if (customerOpt.isPresent()) {
            Customer existingCustomer = customerOpt.get();
            boolean needsUpdate = false;
            if (name != null && !name.trim().isEmpty() && !name.equals(existingCustomer.getFullName())) {
                existingCustomer.setFullName(name);
                needsUpdate = true;
            }
            if (phone != null && !phone.trim().isEmpty() && !phone.equals(existingCustomer.getPhoneNumber())) {
                existingCustomer.setPhoneNumber(phone);
                needsUpdate = true;
            }
            if (address != null && (existingCustomer.getAddress() == null || !address.equals(existingCustomer.getAddress()))) {
                existingCustomer.setAddress(address.trim().isEmpty() ? null : address);
                needsUpdate = true;
            }
            if (needsUpdate) {
                return customerRepository.save(existingCustomer);
            }
            return existingCustomer;

            // chua thi tao moi
        } else {
            if (name == null || name.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
                throw new RuntimeException("Vui lòng cung cấp đủ thông tin khách hàng mới.");
            }
            Customer newCustomer = new Customer();
            newCustomer.setFullName(name);
            newCustomer.setEmail(email);
            newCustomer.setPhoneNumber(phone);
            newCustomer.setAddress(address);
            return customerRepository.save(newCustomer);
        }
    }
}