package com.jon.user_manager.user.userDto;

import com.jon.user_manager.user.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponsedDTO {
    private Long id;
    private String userName;
    private String email;
    private Role role;
}
