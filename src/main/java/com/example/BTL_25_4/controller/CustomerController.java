package com.example.BTL_25_4.controller;

import com.example.BTL_25_4.dto.CustomerDTO;
import com.example.BTL_25_4.exception.ResourceNotFoundException;
import com.example.BTL_25_4.exception.ValidationException;
import com.example.BTL_25_4.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/customers") // Đặt prefix /admin cho quản lý
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Hiển thị danh sách khách hàng
    @GetMapping
    public String listCustomers(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<CustomerDTO> customers;
        if (keyword != null && !keyword.isEmpty()) {
            customers = customerService.searchCustomers(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            customers = customerService.findAllCustomers();
        }
        model.addAttribute("customers", customers);
        model.addAttribute("pageTitle", "Danh sách Khách hàng");
        return "customers/list"; // Trả về templates/customers/list.html
    }

    // Hiển thị form thêm khách hàng
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("customerDTO", new CustomerDTO());
        model.addAttribute("pageTitle", "Thêm Khách hàng mới");
        model.addAttribute("isEditMode", false);
        return "customers/form"; // Trả về templates/customers/form.html
    }

    // Xử lý thêm khách hàng
    @PostMapping("/add")
    public String addCustomer(@Valid @ModelAttribute("customerDTO") CustomerDTO customerDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm Khách hàng mới");
            model.addAttribute("isEditMode", false);
            return "customers/form"; // Quay lại form nếu có lỗi validation
        }
        try {
            customerService.saveCustomer(customerDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
            return "redirect:/admin/customers";
        } catch (ValidationException e) {
            // Thêm lỗi vào bindingResult để hiển thị trên form
            // Giả sử lỗi ValidationException liên quan đến email trùng lặp
            if (e.getMessage().contains("Email")) {
                bindingResult.rejectValue("email", "error.customerDTO", e.getMessage());
            } else {
                // Lỗi validation chung khác (nếu có)
                model.addAttribute("errorMessage", e.getMessage()); // Hoặc dùng bindingResult.reject(...)
            }
            model.addAttribute("pageTitle", "Thêm Khách hàng mới");
            model.addAttribute("isEditMode", false);
            return "customers/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi không xác định khi thêm khách hàng.");
            return "redirect:/admin/customers";
        }
    }

    // Hiển thị form sửa khách hàng
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<CustomerDTO> customerOpt = customerService.findCustomerById(id);
        if (customerOpt.isPresent()) {
            model.addAttribute("customerDTO", customerOpt.get());
            model.addAttribute("pageTitle", "Sửa thông tin Khách hàng");
            model.addAttribute("isEditMode", true);
            return "customers/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng với ID: " + id);
            return "redirect:/admin/customers";
        }
    }

    // Xử lý sửa khách hàng
    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable Long id,
                                 @Valid @ModelAttribute("customerDTO") CustomerDTO customerDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Sửa thông tin Khách hàng");
            model.addAttribute("isEditMode", true);
            return "customers/form";
        }
        try {
            customerService.updateCustomer(id, customerDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin khách hàng thành công!");
            return "redirect:/admin/customers";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/customers";
        } catch (ValidationException e) {
            if (e.getMessage().contains("Email")) {
                bindingResult.rejectValue("email", "error.customerDTO", e.getMessage());
            } else {
                model.addAttribute("errorMessage", e.getMessage());
            }
            model.addAttribute("pageTitle", "Sửa thông tin Khách hàng");
            model.addAttribute("isEditMode", true);
            return "customers/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi không xác định khi cập nhật khách hàng.");
            return "redirect:/admin/customers";
        }
    }

    // Xử lý xóa khách hàng (nên dùng phương thức DELETE, nhưng GET đơn giản hơn với link)
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            // Có thể do ràng buộc khóa ngoại nếu khách hàng có booking liên quan (nếu đã refactor)
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa khách hàng này. Có thể do khách hàng đang có liên kết dữ liệu khác.");
        }
        return "redirect:/admin/customers";
    }

}