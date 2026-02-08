package com.bizvenue.backend.controller;

import com.bizvenue.backend.dto.InvoiceDTO;
import com.bizvenue.backend.service.InvoiceService;
import com.bizvenue.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices(@RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) Integer companyId) {
        System.out.println(
                "DEBUG INVOICES: Fetching invoices. Params: clientId=" + clientId + ", companyId=" + companyId);
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
                    System.out.println("DEBUG INVOICES: Forced Company ID check. User Email: " + email
                            + ", Resulting CompanyId: " + companyId);
                }
            }
        }

        List<InvoiceDTO> results;
        if (clientId != null) {
            results = invoiceService.getInvoicesByClient(clientId);
        } else if (companyId != null) {
            results = invoiceService.getInvoicesByCompany(companyId);
        } else {
            results = invoiceService.getAllInvoices();
        }
        System.out.println("DEBUG INVOICES: Returning " + results.size() + " invoices.");
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable int id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        System.out.println("DEBUG INVOICES: Manual creation attempt...");
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            if (email != null && !email.equals("anonymousUser")) {
                com.bizvenue.backend.entity.User user = userRepository.findByEmail(email).orElse(null);
                if (user != null && user.getRole() == com.bizvenue.backend.entity.enums.Role.COMPANY) {
                    invoiceDTO.setCompanyId(user.getId());
                    System.out.println("DEBUG INVOICES: Manual creation forced to CompanyID " + user.getId());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(invoiceDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable int id, @RequestBody InvoiceDTO invoiceDTO) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoiceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable int id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable int id) {
        byte[] pdfBytes = invoiceService.generatePdf(id);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"invoice-" + id + ".pdf\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> payInvoice(@PathVariable int id) {
        invoiceService.payInvoice(id);
        return ResponseEntity.ok().build();
    }
}
