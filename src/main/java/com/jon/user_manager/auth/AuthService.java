package com.jon.user_manager.auth;

import com.jon.user_manager.auth.authDto.LoginRequestDTO;
import com.jon.user_manager.auth.authDto.LoginResponseDTO;
import com.jon.user_manager.user.User;
import com.jon.user_manager.user.UserRepository;
import com.jon.user_manager.util.exceptionHandler.BadCredentialsException;
import com.jon.user_manager.util.exceptionHandler.BadToken;
import com.jon.user_manager.util.exceptionHandler.ResourceNotFoundException;
import com.jon.user_manager.util.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.expiration}")
    private long expiration;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(ResourceNotFoundException::new);

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException();
        }

        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "role", "USER"
        );

        String accessToken = jwtUtils.generateToken(user.getEmail(), claims);
        String refreshToken = refreshTokenService.generateRefreshToken(user);

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    public LoginResponseDTO refreshAccessToken(String refreshToken) {
        String tokenHash = passwordEncoder.encode(refreshToken);
        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByTokenHash(tokenHash);

        if (tokenEntity.isEmpty() || tokenEntity.get().isRevoked()) {
            throw new BadToken();
        }

        User user = tokenEntity.get().getUser();
        String newAccessToken = jwtUtils.generateToken(user.getEmail(), Map.of());
        String newRefreshToken = refreshTokenService.rotate(refreshToken, user);

        return new LoginResponseDTO(newAccessToken, newRefreshToken);
    }

    public void logout(String token) {
        String email = jwtUtils.getSubject(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(ResourceNotFoundException::new);
        refreshTokenService.revoke(user);
    }
}
