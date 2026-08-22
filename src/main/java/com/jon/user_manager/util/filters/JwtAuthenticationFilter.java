package com.jon.user_manager.util.filters;

import com.jon.user_manager.user.User;
import com.jon.user_manager.user.UserRepository;
import com.jon.user_manager.util.security.JwtLogoutHandler;
import com.jon.user_manager.util.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // implements Filter

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final JwtLogoutHandler jwtLogoutHandler;

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getRequestURI().contains("/api/v1/auth/login") || request.getRequestURI().contains("/api/v1/auth/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        // public void doFilter(ServletRequest req, ServletResponse response,  FilterChain chain) throws ServletException, IOException {
        // HttpServletRequest request = (HttpServletRequest) req;
        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if(jwtUtils.isValid(token) && !jwtLogoutHandler.isTokenBlacklisted(token)) {
                String email = jwtUtils.getSubject(token);
                User user = userRepository.findByEmail(email).orElse(null);
                if(user != null) {
                    // TODO Defines ROLE for User entity
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");

                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(authority)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
