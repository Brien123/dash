package com.example.dash.authentication.dto;

import com.example.dash.user.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDto {
    private UserDto user;
    private String accessToken;
    private String refreshToken;
}
