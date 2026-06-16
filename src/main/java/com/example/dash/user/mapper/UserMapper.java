package com.example.dash.user.mapper;

import com.example.dash.authentication.dto.RegisterDto;
import com.example.dash.user.dto.UserDto;
import com.example.dash.user.model.Role;
import com.example.dash.user.model.User;

public class UserMapper {
    public static UserDto toDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setVerified(user.isVerified());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());

        return userDto;
    }

    public static User toEntity(RegisterDto registerDto){
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setRole(Role.USER);

        return user;
    }
}
