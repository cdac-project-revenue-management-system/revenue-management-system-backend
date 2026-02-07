package com.bizvenue.backend.dto;

import com.bizvenue.backend.entity.enums.ClientStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientDTO extends UserDTO {
    private String phone;
    private String billingInfo;
    private ClientStatus status;
    private String companyName;
    private BigDecimal totalSpent;
    private LocalDateTime lastActivity;

    // Frontend compatibility fields
    private int subscriptions; // count of subscriptions

    public String getFormattedId() {
        return String.format("CLI-%03d", getId());
    }

    public String getStatusString() {
        return status != null ? status.name().toLowerCase() : null;
    }

    public LocalDateTime getJoinedAt() {
        return getCreatedAt();
    }
}
