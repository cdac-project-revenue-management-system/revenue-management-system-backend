package com.bizvenue.backend.dto;

import com.bizvenue.backend.entity.enums.InvoiceStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceDTO {
    private int id;
    private int clientId;
    private Integer companyId;
    private Integer subscriptionId;
    private BigDecimal amount;
    private InvoiceStatus status;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private Integer items;

    // Frontend compatibility fields
    private String client; // Client Name
    private String clientEmail;

    public String getFormattedId() {
        return String.format("INV-2024-%04d", id); // Using frontend style INV-2024-xxxx
    }

    public String getStatusString() {
        return status != null ? status.name().toLowerCase() : null;
    }
}
