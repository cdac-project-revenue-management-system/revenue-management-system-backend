package com.bizvenue.backend.dto;

import com.bizvenue.backend.entity.enums.ProductStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private int id;
    private int companyId;
    private String name;
    private String description;
    private ProductStatus status;
    private BigDecimal revenue;
    private Integer activeSubscriptions;
    private LocalDateTime createdAt;

    // Frontend compatibility fields
    private int plansCount;

    public String getFormattedId() {
        return String.format("PROD-%03d", id);
    }

    public String getStatusString() {
        return status != null ? status.name().toLowerCase() : null;
    }
}
