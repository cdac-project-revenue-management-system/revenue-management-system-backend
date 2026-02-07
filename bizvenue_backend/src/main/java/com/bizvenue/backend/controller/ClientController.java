package com.bizvenue.backend.controller;

import com.bizvenue.backend.dto.ClientDTO;
import com.bizvenue.backend.service.ClientService;
import com.bizvenue.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients(@RequestParam(required = false) Integer companyId) {
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

        if (companyId != null) {
            return ResponseEntity.ok(clientService.getClientsByCompany(companyId));
        }
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable int id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO clientDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(clientDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable int id, @RequestBody ClientDTO clientDTO) {
        return ResponseEntity.ok(clientService.updateClient(id, clientDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable int id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendClient(@PathVariable int id) {
        clientService.suspendClient(id);
        return ResponseEntity.ok().build();
    }
}
