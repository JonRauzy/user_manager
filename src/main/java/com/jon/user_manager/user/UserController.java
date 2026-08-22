package com.jon.user_manager.user;

import com.jon.user_manager.auth.authDto.LoginRequestDTO;
import com.jon.user_manager.user.userDto.UserRegisterDTO;
import com.jon.user_manager.user.userDto.UserResponsedDTO;
import com.jon.user_manager.user.userDto.UserUpdateDTO;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Getter
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponsedDTO> findAll(){
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserResponsedDTO findById(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @GetMapping("/with-email")
    public UserResponsedDTO findByEmail(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        return userService.findByEmail(loginRequestDTO.getEmail());
    }

    @PostMapping
    public UserResponsedDTO register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    @PutMapping("/{userId}")
    public UserResponsedDTO update(@Valid @RequestBody UserUpdateDTO userUpdateDTO, @PathVariable Long userId) {
        return userService.update(userUpdateDTO, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}
