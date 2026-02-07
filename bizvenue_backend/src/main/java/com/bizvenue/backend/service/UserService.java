package com.bizvenue.backend.service;

import com.bizvenue.backend.dto.UserDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();

    UserDTO getUserById(int id);

    void deleteUser(int id);
}
