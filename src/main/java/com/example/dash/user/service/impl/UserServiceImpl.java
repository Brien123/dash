package com.example.dash.user.service.impl;

import com.example.dash.authentication.dto.RegisterDto;
import com.example.dash.user.dto.UserDto;
import com.example.dash.user.mapper.UserMapper;
import com.example.dash.user.model.User;
import com.example.dash.user.repository.UserRepository;
import com.example.dash.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto updateUser(RegisterDto registerDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("user not found"));
        if(registerDto.getUsername()!=null){
            user.setUsername(registerDto.getUsername());
        } else if (registerDto.getEmail()!=null) {
            user.setEmail(registerDto.getEmail());
        }
        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        return UserMapper.toDto(user);
    }

    @Override
    public Page<UserDto> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toDto);
    }

    @Override
    public void changeUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        if(user.isVerified()) {
            user.setVerified(false);
            userRepository.save(user);
        } else {
            user.setVerified(true);
            userRepository.save(user);
        }
    }
}
