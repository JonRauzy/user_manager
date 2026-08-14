package com.jon.user_manager.user.userDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    @NotBlank
    private String userName;

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
