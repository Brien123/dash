package com.example.dash.user.service;

import com.example.dash.authentication.dto.RegisterDto;
import com.example.dash.user.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto updateUser(RegisterDto registerDto, String email);
    UserDto getUser(Long userId);
    Page<UserDto> listUsers(Pageable pageable);
    void changeUserStatus(Long userId);
}
