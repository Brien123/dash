package com.example.dash.authentication.controller;

import com.example.dash.authentication.dto.*;
import com.example.dash.authentication.service.AuthenticationService;
import com.example.dash.common.dto.ApiResponse;
import com.example.dash.user.dto.UserDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @SecurityRequirements
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterDto registerDto) {
        UserDto user = authenticationService.register(registerDto);
        return ResponseEntity.ok(ApiResponse.success("Registration successful. OTP sent to email.", user));
    }

    @PostMapping("/verify-email")
    @SecurityRequirements
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestBody VerifyEmailDto verifyEmailDto) {
        authenticationService.verifyEmail(verifyEmailDto);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
    }

    @PostMapping("/resend-code")
    @SecurityRequirements
    public ResponseEntity<ApiResponse<Void>> resendCode(@RequestBody ResendCodeDto resendCodeDto) {
        authenticationService.resendCode(resendCodeDto);
        return ResponseEntity.ok(ApiResponse.success("OTP resent to email"));
    }

    @PostMapping("/login")
    @SecurityRequirements
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody LoginDto loginDto) {
        AuthResponseDto response = authenticationService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/forgot-password")
    @SecurityRequirements
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        authenticationService.forgotPasswordRequest(forgotPasswordDto);
        return ResponseEntity.ok(ApiResponse.success("Password reset code sent to email"));
    }

    @PostMapping("/reset-password")
    @SecurityRequirements
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        authenticationService.resetPasswordConfirm(resetPasswordDto);
        return ResponseEntity.ok(ApiResponse.success("Password reset successful"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        authenticationService.changePassword(changePasswordDto);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @PostMapping("/refresh")
    @SecurityRequirements
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthResponseDto response = authenticationService.refresh(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @SecurityRequirements
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest logoutRequest) {
        authenticationService.logout(logoutRequest);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
