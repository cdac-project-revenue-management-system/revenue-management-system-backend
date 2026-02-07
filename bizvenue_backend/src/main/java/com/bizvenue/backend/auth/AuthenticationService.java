package com.bizvenue.backend.auth;

import com.bizvenue.backend.config.JwtService;
import com.bizvenue.backend.entity.User;
import com.bizvenue.backend.repository.UserRepository;
import com.bizvenue.backend.service.RemoteLoggerService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final RemoteLoggerService logger;

        public AuthenticationResponse register(RegisterRequest request) {
                if (repository.findByEmail(request.getEmail()).isPresent()) {
                        throw new RuntimeException("Email already exists: " + request.getEmail());
                }
                User user;
                if (request.getRole() == com.bizvenue.backend.entity.enums.Role.CLIENT) {
                        String clientCompanyName = request.getCompany_name();
                        if (clientCompanyName == null || clientCompanyName.trim().isEmpty()) {
                                clientCompanyName = "Private Client";
                        }

                        user = com.bizvenue.backend.entity.Client.builder()
                                        .fullName(request.getFull_name())
                                        .email(request.getEmail())
                                        .password(passwordEncoder.encode(request.getPassword()))
                                        .role(request.getRole())
                                        .companyName(clientCompanyName)
                                        .clientCompanyName(clientCompanyName)
                                        .phone(request.getPhone())
                                        .clientPhone(request.getPhone())
                                        .status(com.bizvenue.backend.entity.enums.ClientStatus.INACTIVE)
                                        .totalSpent(java.math.BigDecimal.ZERO)
                                        .lastActivity(java.time.LocalDateTime.now())
                                        .build();
                } else if (request.getRole() == com.bizvenue.backend.entity.enums.Role.COMPANY) {
                        user = com.bizvenue.backend.entity.Company.builder()
                                        .fullName(request.getFull_name())
                                        .email(request.getEmail())
                                        .password(passwordEncoder.encode(request.getPassword()))
                                        .role(request.getRole())
                                        .companyName(request.getCompany_name())
                                        .businessName(request.getCompany_name())
                                        .phone(request.getPhone())
                                        .businessPhone(request.getPhone())
                                        .build();
                } else {
                        user = User.builder()
                                        .fullName(request.getFull_name())
                                        .email(request.getEmail())
                                        .password(passwordEncoder.encode(request.getPassword()))
                                        .role(request.getRole())
                                        .companyName(request.getCompany_name())
                                        .phone(request.getPhone())
                                        .build();
                }
                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                logger.info("User registered successfully: " + user.getEmail(), "Anonymous");
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .id(user.getId())
                                .role(user.getRole().name())
                                .fullName(user.getFullName())
                                .companyName(user.getCompanyName())
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow();
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .id(user.getId())
                                .role(user.getRole().name())
                                .fullName(user.getFullName())
                                .companyName(user.getCompanyName())
                                .build();
        }
}
