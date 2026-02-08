package com.bizvenue.backend.dto;

import com.bizvenue.backend.entity.enums.PlanInterval;
import com.bizvenue.backend.entity.enums.PlanStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PlanDTO {
    private int id;
    private int productId;
    private String name;
    private BigDecimal price;
    private PlanInterval interval;
    private List<String> features;
    private Boolean isPopular;
    private PlanStatus status;

    // Frontend compatibility fields
    private String product; // Product Name
    private int companyId;
    private String companyName;
    private int subscribers;

    public String getFormattedId() {
        return String.valueOf(id); // Frontend uses "1", "2" for plans mostly
    }

    public String getIntervalString() {
        return interval != null ? interval.name().toLowerCase() : null;
    }

    public String getStatusString() {
        return status != null ? status.name().toLowerCase() : null;
    }
}
