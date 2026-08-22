package com.jon.user_manager.auth.authDto;

import com.jon.user_manager.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    User loginRequestDtoToEntity(LoginRequestDTO loginRequestDTO);
}
