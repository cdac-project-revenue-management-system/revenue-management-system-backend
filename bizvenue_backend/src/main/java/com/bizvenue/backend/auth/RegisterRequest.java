package com.bizvenue.backend.auth;

import com.bizvenue.backend.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String full_name;
    private String email;
    private String password;
    private Role role;
    private String company_name;
    private String phone;
}
