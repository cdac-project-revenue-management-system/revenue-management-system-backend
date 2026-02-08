package com.bizvenue.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyDTO extends UserDTO {
    private String companyName;
    private String phone;
}
