package com.bizvenue.backend.dto;

import com.bizvenue.backend.entity.enums.SubscriptionStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SubscriptionDTO {
    private int id;
    private int clientId;
    private int planId;
    private SubscriptionStatus status;
    private BigDecimal amount;
    private LocalDateTime startDate;
    private LocalDateTime nextBilling;

    // Frontend compatibility fields
    private String client; // Client Name
    private String clientEmail;
    private String plan; // Plan Name
    private String product; // Product Name
    private String interval; // Plan Interval (monthly/yearly) string

    public String getFormattedId() {
        return String.format("SUB-%03d", id);
    }

    public String getStatusString() {
        return status != null ? status.name().toLowerCase() : null;
    }
}
