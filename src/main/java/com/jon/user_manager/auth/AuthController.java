package com.jon.user_manager.auth;

import com.jon.user_manager.auth.authDto.LoginRequestDTO;
import com.jon.user_manager.auth.authDto.LoginResponseDTO;
import com.jon.user_manager.auth.authDto.RefreshTokenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        LoginResponseDTO response = authService.refreshAccessToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        authService.logout(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok("Logout successfull");
    }
}
