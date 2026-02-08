package com.bizvenue.backend.controller;

import com.bizvenue.backend.dto.PlanDTO;
import com.bizvenue.backend.service.PlanService;
import com.bizvenue.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<PlanDTO>> getAllPlans(@RequestParam(required = false) Integer productId,
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

        if (productId != null) {
            return ResponseEntity.ok(planService.getPlansByProduct(productId));
        }
        if (companyId != null) {
            return ResponseEntity.ok(planService.getPlansByCompany(companyId));
        }
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable int id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @PostMapping
    public ResponseEntity<PlanDTO> createPlan(@RequestBody PlanDTO planDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.createPlan(planDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanDTO> updatePlan(@PathVariable int id, @RequestBody PlanDTO planDTO) {
        return ResponseEntity.ok(planService.updatePlan(id, planDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable int id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PlanDTO> toggleStatus(@PathVariable int id,
            @RequestBody java.util.Map<String, String> payload) {
        String status = payload.get("status");
        return ResponseEntity.ok(planService.toggleStatus(id, status));
    }
}
