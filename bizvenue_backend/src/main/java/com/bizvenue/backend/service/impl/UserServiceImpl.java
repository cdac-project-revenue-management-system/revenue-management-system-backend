package com.bizvenue.backend.service.impl;

import com.bizvenue.backend.dto.UserDTO;
import com.bizvenue.backend.mapper.EntityMapper;
import com.bizvenue.backend.repository.UserRepository;
import com.bizvenue.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(int id) {
        return userRepository.findById(id)
                .map(mapper::toUserDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
