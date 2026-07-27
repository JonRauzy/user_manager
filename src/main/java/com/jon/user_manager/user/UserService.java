package com.jon.user_manager.user;

import com.jon.user_manager.user.userDto.UserMapper;
import com.jon.user_manager.user.userDto.UserRequestDTO;
import com.jon.user_manager.user.userDto.UserResponsedDTO;
import com.jon.user_manager.util.exceptionHandler.ResourceNotFoundException;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponsedDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserResponsedDTO findById(Long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id)));
    }

    public UserResponsedDTO save(UserRequestDTO userRequestDTO) {
        User user = userMapper.toEntity(userRequestDTO);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserResponsedDTO update(UserRequestDTO userRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));

        user.setUserName(userRequestDTO.getUserName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPhotoUrl(userRequestDTO.getPhotoUrl());

        return userMapper.toDto(userRepository.save(user));
    }

    public void delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
        userRepository.delete(user);
    }
}
