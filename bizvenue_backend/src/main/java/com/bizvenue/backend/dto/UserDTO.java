package com.bizvenue.backend.dto;

import com.bizvenue.backend.entity.enums.Role;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private int id;
    private String email;
    private String fullName;
    private String password;
    private Role role;
    private LocalDateTime createdAt;
}
