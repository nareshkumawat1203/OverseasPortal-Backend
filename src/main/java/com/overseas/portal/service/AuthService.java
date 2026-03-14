package com.overseas.portal.service;

import com.overseas.portal.config.JwtService;
import com.overseas.portal.dto.AuthResponse;
import com.overseas.portal.dto.LoginRequest;
import com.overseas.portal.dto.RegisterRequest;
import com.overseas.portal.entity.*;
import com.overseas.portal.enums.ProviderType;
import com.overseas.portal.enums.RoleType;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered: " + request.getEmail());
        }

        RoleType roleType = request.getRole();
        if (roleType == RoleType.ROLE_ADMIN) {
            throw new IllegalArgumentException("Cannot self-register as ADMIN");
        }

        Role role = roleRepository.findByName(roleType)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleType));

        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .roles(Set.of(role))
            .emailVerified(true) // Set false in production; use email verification
            .active(true)
            .emailVerificationToken(UUID.randomUUID().toString())
            .build();

        User savedUser = userRepository.save(user);

        if (roleType == RoleType.ROLE_STUDENT) {
            StudentProfile profile = StudentProfile.builder().user(savedUser).build();
            studentProfileRepository.save(profile);
        } else if (roleType == RoleType.ROLE_PROVIDER) {
            ProviderProfile profile = ProviderProfile.builder()
                .user(savedUser)
                .companyName(request.getCompanyName() != null ? request.getCompanyName() : savedUser.getFullName())
                .providerType(request.getProviderType() != null
                    ? ProviderType.valueOf(request.getProviderType())
                    : ProviderType.AGENCY)
                .build();
            providerProfileRepository.save(profile);
        }

        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);
        return buildAuthResponse(user, newAccessToken, refreshToken);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        Set<String> roles = user.getRoles().stream()
            .map(r -> r.getName().name())
            .collect(Collectors.toSet());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .roles(roles)
            .emailVerified(user.isEmailVerified())
            .build();
    }
}
