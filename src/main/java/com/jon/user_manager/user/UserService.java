package com.jon.user_manager.user;

import com.jon.user_manager.user.userDto.UserMapper;
import com.jon.user_manager.user.userDto.UserRegisterDTO;
import com.jon.user_manager.user.userDto.UserResponsedDTO;
import com.jon.user_manager.user.userDto.UserUpdateDTO;
import com.jon.user_manager.util.exceptionHandler.ResourceExist;
import com.jon.user_manager.util.exceptionHandler.ResourceNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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

    public UserResponsedDTO findByEmail(String userEmail) {
        return userMapper.toDto(userRepository.findByEmail(userEmail)
                .orElseThrow(ResourceNotFoundException::new));
    }

    public UserResponsedDTO register(UserRegisterDTO userRegisterDTO) {
        if(userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            throw new ResourceExist(User.class);
        }
        userRegisterDTO.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        User user = userRepository.save(userMapper.registerDtoToEntity(userRegisterDTO));
        return userMapper.toDto(user);
    }

    public UserResponsedDTO update(UserUpdateDTO userUpdateDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));

        user.setUserName(userUpdateDTO.getUserName());
        user.setEmail(userUpdateDTO.getEmail());

        return userMapper.toDto(userRepository.save(user));
    }

    public void delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
        userRepository.delete(user);
    }
}
