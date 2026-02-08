package com.bizvenue.backend.controller;

import com.bizvenue.backend.dto.ProductDTO;
import com.bizvenue.backend.service.ProductService;
import com.bizvenue.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(@RequestParam(required = false) Integer companyId) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            if (email != null && !email.equals("anonymousUser")) {
                com.bizvenue.backend.entity.User user = userRepository.findByEmail(email).orElse(null);
                if (user != null && user.getRole() == com.bizvenue.backend.entity.enums.Role.COMPANY) {
                    companyId = user.getId();
                    System.out.println("DEBUG FILTER: Restricted to CompanyID " + companyId);
                }
            }
        }

        if (companyId != null) {
            return ResponseEntity.ok(productService.getProductsByCompany(companyId));
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        System.out.println("DEBUG CREATE: Starting creation...");
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            System.out.println("DEBUG CREATE: Email context: " + email);
            if (email != null && !email.equals("anonymousUser")) {
                com.bizvenue.backend.entity.User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    System.out.println("DEBUG CREATE: Found User ID=" + user.getId() + " Role=" + user.getRole());
                    if (user.getRole() == com.bizvenue.backend.entity.enums.Role.COMPANY) {
                        productDTO.setCompanyId(user.getId());
                        System.out.println("DEBUG CREATE: Forced CompanyID to " + user.getId());
                    } else {
                        System.out.println("DEBUG CREATE: Role is NOT COMPANY. It is " + user.getRole());
                    }
                } else {
                    System.out.println("DEBUG CREATE: User not found in DB for email " + email);
                }
            }
        } else {
            System.out.println("DEBUG CREATE: No Authentication found.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable int id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
