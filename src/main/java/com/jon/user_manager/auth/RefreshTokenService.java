package com.jon.user_manager.auth;

import com.jon.user_manager.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt

    // Générer un nouveau Refresh Token
    public String generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        String hash = passwordEncoder.encode(token);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hash);
        refreshToken.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS)); // 7 jours
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
        return token; // Retourne le token NON haché (pour le client)
    }

    // Valider un Refresh Token
    public boolean isValid(String rawToken) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByTokenHash(rawToken);
        return refreshToken.isPresent() &&
                !refreshToken.get().isRevoked() &&
                refreshToken.get().getExpiresAt().isAfter(Instant.now());
    }

    // Révoker un Refresh Token (déconnexion)
    public void revoke(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    // Rotation : invalider l’ancien et en générer un nouveau
    public String rotate(String oldRawToken, User user) {
        revokeByToken(oldRawToken); // Invalide l’ancien
        return generateRefreshToken(user); // Génère un nouveau
    }

    private void revokeByToken(String rawToken) {
        refreshTokenRepository.findByTokenHash(rawToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }
}
