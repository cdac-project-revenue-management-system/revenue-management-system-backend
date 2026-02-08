package com.bizvenue.backend.controller;

import com.bizvenue.backend.dto.SubscriptionDTO;
import com.bizvenue.backend.service.SubscriptionService;
import com.bizvenue.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions(@RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) Integer companyId) {
        // Robust Security Check using UserRepository lookup
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            if (email != null && !email.equals("anonymousUser")) {
                com.bizvenue.backend.entity.User user = userRepository.findByEmail(email).orElse(null);
                if (user != null && user.getRole() == com.bizvenue.backend.entity.enums.Role.COMPANY) {
                    // Force filter by Company ID for Company Users
                    companyId = user.getId();
                }
            }
        }

        if (clientId != null) {
            return ResponseEntity.ok(subscriptionService.getSubscriptionsByClient(clientId));
        }
        if (companyId != null) {
            return ResponseEntity.ok(subscriptionService.getSubscriptionsByCompany(companyId));
        }
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscriptionById(@PathVariable int id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    @PostMapping
    public ResponseEntity<SubscriptionDTO> createSubscription(@RequestBody SubscriptionDTO subscriptionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.createSubscription(subscriptionDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> updateSubscription(@PathVariable int id,
            @RequestBody SubscriptionDTO subscriptionDTO) {
        return ResponseEntity.ok(subscriptionService.updateSubscription(id, subscriptionDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable int id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<SubscriptionDTO> renewSubscription(@PathVariable int id) {
        return ResponseEntity.ok(subscriptionService.renewSubscription(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionDTO> cancelSubscription(@PathVariable int id) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
    }
}
