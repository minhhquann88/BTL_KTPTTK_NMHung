package com.example.BTL_25_4.service.impl;

import com.example.BTL_25_4.dto.CustomerDTO;
import com.example.BTL_25_4.entity.Customer;
import com.example.BTL_25_4.exception.ResourceNotFoundException;
import com.example.BTL_25_4.exception.ValidationException;
import com.example.BTL_25_4.repository.CustomerRepository;
import com.example.BTL_25_4.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ... (Các phương thức findAllCustomers, findCustomerById giữ nguyên) ...
    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findCustomerById(Long id) {
        return customerRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    @Transactional
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) throws ValidationException {
        // CẬP NHẬT: Kiểm tra sự tồn tại của cặp email-sđt
        if (customerRepository.findByEmailAndPhoneNumber(customerDTO.getEmail(), customerDTO.getPhoneNumber()).isPresent()) {
            throw new ValidationException("Email '" + customerDTO.getEmail() + "' và Số điện thoại '" + customerDTO.getPhoneNumber() + "' đã được đăng ký.");
        }
        // Kiểm tra riêng email hoặc sđt có tồn tại với cặp khác không (để thông báo lỗi rõ ràng hơn)
        if (customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            throw new ValidationException("Email '" + customerDTO.getEmail() + "' đã tồn tại với một số điện thoại khác.");
        }
        if (customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber()).isPresent()) {
            throw new ValidationException("Số điện thoại '" + customerDTO.getPhoneNumber() + "' đã tồn tại với một email khác.");
        }

        Customer customer = mapToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return mapToDTO(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws ResourceNotFoundException, ValidationException {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));

        // CẬP NHẬT: Kiểm tra email và sđt mới có trùng với người khác không
        Optional<Customer> customerWithSamePair = customerRepository.findByEmailAndPhoneNumber(customerDTO.getEmail(), customerDTO.getPhoneNumber());
        if (customerWithSamePair.isPresent() && !customerWithSamePair.get().getId().equals(id)) {
            throw new ValidationException("Email và số điện thoại này đã được sử dụng bởi khách hàng khác.");
        }
        // Kiểm tra riêng email hoặc sđt có bị trùng với cặp khác không
        Optional<Customer> customerWithSameEmail = customerRepository.findByEmail(customerDTO.getEmail());
        if (customerWithSameEmail.isPresent() && !customerWithSameEmail.get().getId().equals(id)) {
            throw new ValidationException("Email '" + customerDTO.getEmail() + "' đã được sử dụng bởi khách hàng khác với số điện thoại khác.");
        }
        Optional<Customer> customerWithSamePhone = customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber());
        if (customerWithSamePhone.isPresent() && !customerWithSamePhone.get().getId().equals(id)) {
            throw new ValidationException("Số điện thoại '" + customerDTO.getPhoneNumber() + "' đã được sử dụng bởi khách hàng khác với email khác.");
        }

        // Cập nhật thông tin
        existingCustomer.setFullName(customerDTO.getFullName());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
        existingCustomer.setAddress(customerDTO.getAddress());
        // Không cập nhật ngày đăng ký

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return mapToDTO(updatedCustomer);
    }

    // ... (deleteCustomer, searchCustomers giữ nguyên) ...
    @Override
    @Transactional
    public void deleteCustomer(Long id) throws ResourceNotFoundException {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllCustomers();
        }
        // Phương thức tìm kiếm gần đúng theo tên
        return customerRepository.findByFullNameContainingIgnoreCase(keyword.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- IMPLEMENT PHƯƠNG THỨC findAndValidateCustomer ---
    @Override
    @Transactional // Quan trọng: Cần Transaction vì có thể vừa đọc vừa ghi
    public Customer findAndValidateCustomer(String name, String email, String phone, String address) throws ValidationException {
        if (email == null || email.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            log.warn("Attempted to find or validate customer with null or empty email/phone.");
            throw new ValidationException("Email và Số điện thoại không được để trống khi đặt xe.");
        }

        Optional<Customer> exactMatchOpt = customerRepository.findByEmailAndPhoneNumber(email, phone);

        if (exactMatchOpt.isPresent()) {
            // Tìm thấy khách hàng với ĐÚNG email và sđt -> Trả về khách hàng này
            log.info("Found existing customer matching email {} and phone {}", email, phone);
            return exactMatchOpt.get();
        } else {
            // Không tìm thấy cặp email-sđt này. Kiểm tra xem email hoặc sđt đã tồn tại riêng lẻ chưa
            Optional<Customer> emailExistsOpt = customerRepository.findByEmail(email);
            if (emailExistsOpt.isPresent()) {
                // Email đã tồn tại nhưng với SĐT khác -> Lỗi Validation
                log.warn("Validation failed: Email {} exists with a different phone number.", email);
                throw new ValidationException("Email '" + email + "' đã được đăng ký với một số điện thoại khác. Vui lòng kiểm tra lại hoặc sử dụng số điện thoại đã đăng ký.");
            }

            Optional<Customer> phoneExistsOpt = customerRepository.findByPhoneNumber(phone);
            if (phoneExistsOpt.isPresent()) {
                // SĐT đã tồn tại nhưng với Email khác -> Lỗi Validation
                log.warn("Validation failed: Phone number {} exists with a different email.", phone);
                throw new ValidationException("Số điện thoại '" + phone + "' đã được đăng ký với một email khác. Vui lòng kiểm tra lại hoặc sử dụng email đã đăng ký.");
            }

            // Cả email và sđt đều chưa có -> Tạo khách hàng mới
            log.info("Creating new customer with email {} and phone {}", email, phone);
            Customer newCustomer = new Customer();
            newCustomer.setFullName(name != null ? name : "N/A"); // Có thể yêu cầu tên không được null
            newCustomer.setEmail(email);
            newCustomer.setPhoneNumber(phone);
            newCustomer.setAddress(address);
            return customerRepository.save(newCustomer); // Lưu khách hàng mới
        }
    }
    // --- HẾT PHẦN IMPLEMENT ---

    private CustomerDTO mapToDTO(Customer customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getRegistrationDate()
        );
    }

    private Customer mapToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        // ID và registrationDate thường không set ở đây khi tạo mới
        customer.setFullName(customerDTO.getFullName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setAddress(customerDTO.getAddress());
        return customer;
    }
}