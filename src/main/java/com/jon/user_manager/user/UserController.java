package com.jon.user_manager.user;

import com.jon.user_manager.user.userDto.UserRequestDTO;
import com.jon.user_manager.user.userDto.UserResponsedDTO;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public UserResponsedDTO save(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.save(userRequestDTO);
    }

    @PutMapping("/{userId}")
    public UserResponsedDTO update(@Valid @RequestBody UserRequestDTO userRequestDTO, @PathVariable Long userId) {
        return userService.update(userRequestDTO, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }
}
