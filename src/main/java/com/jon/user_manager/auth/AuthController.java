package com.jon.user_manager.auth;

import com.jon.user_manager.auth.authDto.LoginRequestDTO;
import com.jon.user_manager.auth.authDto.LoginResponseDTO;
import com.jon.user_manager.user.User;
import com.jon.user_manager.user.UserRepository;
import com.jon.user_manager.util.exceptionHandler.ResourceNotFoundException;
import com.jon.user_manager.util.security.JwtLogoutHandler;
import com.jon.user_manager.util.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Getter
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final JwtLogoutHandler logoutHandler;

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(ResourceNotFoundException::new);

        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())){
            throw new RuntimeException("Nope"); // TODO : exception
        }

        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "role", "USER"
        );

        String token = jwtUtils.generateToken(user.getEmail(), claims);
        return new LoginResponseDTO(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        logoutHandler.logout(request, response, null);
        return ResponseEntity.ok("Logout successfull");
    }
}
