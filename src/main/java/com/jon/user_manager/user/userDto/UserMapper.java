package com.jon.user_manager.user.userDto;

import com.jon.user_manager.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponsedDTO toDto(User user);
    User toEntity(UserRequestDTO userRequestDto);
}
