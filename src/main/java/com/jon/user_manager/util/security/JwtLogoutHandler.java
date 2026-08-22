package com.jon.user_manager.util.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtLogoutHandler implements LogoutHandler {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet(); // Ou utilise Redis

    @Override
    public void logout(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            Authentication authentication
    ) {
        // 1. Récupère le token du header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistedTokens.add(token); // Ajoute à la blacklist
        }
        // 2. Réponse vide (le frontend doit supprimer le token)
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // Méthode pour vérifier si un token est blacklisté (à appeler dans JwtAuthenticationFilter)
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
