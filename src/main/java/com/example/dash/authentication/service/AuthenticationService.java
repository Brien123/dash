package com.example.dash.authentication.service;

import com.example.dash.authentication.dto.*;
import com.example.dash.user.dto.UserDto;

public interface AuthenticationService {
    UserDto register(RegisterDto registerDto);
    AuthResponseDto login(LoginDto loginDto);
    void verifyEmail(VerifyEmailDto verifyEmailDto);
    void resendCode(ResendCodeDto resendCodeDto);
    void forgotPasswordRequest(ForgotPasswordDto forgotPasswordDto);
    void resetPasswordConfirm(ResetPasswordDto resetPasswordDto);
    void changePassword(ChangePasswordDto changePasswordDto);
    AuthResponseDto refresh(RefreshTokenRequest refreshTokenRequest);
    void logout(LogoutRequest logoutRequest);
}
