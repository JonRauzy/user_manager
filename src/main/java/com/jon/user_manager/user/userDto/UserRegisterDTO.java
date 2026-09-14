package com.jon.user_manager.user.userDto;

import com.jon.user_manager.user.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {
    @NotBlank
    private String userName;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private Role role;
}
