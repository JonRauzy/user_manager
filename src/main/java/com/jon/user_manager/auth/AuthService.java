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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(ResourceNotFoundException::new);

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException();
        }

        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    public LoginResponseDTO refreshAccessToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(BadToken::new);

        if (!isRefreshTokenValid(refreshTokenEntity)) {
            throw new BadToken();
        }

        User user = userRepository.findById(refreshTokenEntity.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(refreshTokenEntity.getUser().getId()));

        String newAccessToken = jwtUtils.generateAccessToken(user);
        String newRefreshToken = rotate(refreshToken, user);

        saveRefreshToken(user, newRefreshToken);

        return new LoginResponseDTO(newAccessToken, newRefreshToken);
    }

    public void logout(String token) {
        revokeByToken(token);
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS)); // 7 jours
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
    }

    private boolean isRefreshTokenValid(RefreshToken refreshToken) {
        return !refreshToken.isRevoked() &&
                refreshToken.getExpiresAt().isAfter(Instant.now());
    }

    private void revoke(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private void revokeByToken(String rawToken) {
        refreshTokenRepository.findByToken(rawToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private String rotate(String oldRawToken, User user) {
        revokeByToken(oldRawToken);
        return jwtUtils.generateRefreshToken(user);
    }
}
