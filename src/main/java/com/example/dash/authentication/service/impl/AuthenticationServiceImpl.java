package com.example.dash.authentication.service.impl;

import com.example.dash.authentication.dto.AuthResponseDto;
import com.example.dash.authentication.dto.ChangePasswordDto;
import com.example.dash.authentication.dto.ForgotPasswordDto;
import com.example.dash.authentication.dto.LoginDto;
import com.example.dash.authentication.dto.LogoutRequest;
import com.example.dash.authentication.dto.RefreshTokenRequest;
import com.example.dash.authentication.dto.RegisterDto;
import com.example.dash.authentication.dto.ResendCodeDto;
import com.example.dash.authentication.dto.ResetPasswordDto;
import com.example.dash.authentication.dto.VerifyEmailDto;
import com.example.dash.authentication.service.AuthenticationService;
import com.example.dash.common.Email.service.EmailQueueService;
import com.example.dash.security.JwtTokenProvider;
import com.example.dash.user.dto.UserDto;
import com.example.dash.user.mapper.UserMapper;
import com.example.dash.user.model.User;
import com.example.dash.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailQueueService emailQueueService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserDto register(RegisterDto registerDto) {
        validateEmail(registerDto.getEmail());
        validatePassword(registerDto.getPassword());

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = UserMapper.toEntity(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        String otp = generateOtp();
        redisTemplate.opsForValue().set("otp:" + savedUser.getEmail(), otp, 15, TimeUnit.MINUTES);
        emailQueueService.enqueueOtpEmail(savedUser.getEmail(), otp);

        return UserMapper.toDto(savedUser);
    }

    @Override
    public AuthResponseDto login(LoginDto loginDto) {
        validateEmail(loginDto.getEmail());
        validatePassword(loginDto.getPassword());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Account is not activated. Please verify your OTP.");
        }

        return createAuthResponse(user);
    }

    @Override
    public void verifyEmail(VerifyEmailDto verifyEmailDto) {
        validateEmail(verifyEmailDto.getEmail());

        String storedOtp = (String) redisTemplate.opsForValue().get("otp:" + verifyEmailDto.getEmail());
        if (storedOtp == null) {
            throw new RuntimeException("OTP not found or expired");
        }
        if (!storedOtp.equals(verifyEmailDto.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(verifyEmailDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        redisTemplate.delete("otp:" + verifyEmailDto.getEmail());
    }

    @Override
    public void resendCode(ResendCodeDto resendCodeDto) {
        validateEmail(resendCodeDto.getEmail());

        if (!userRepository.existsByEmail(resendCodeDto.getEmail())) {
            throw new RuntimeException("User not found");
        }

        String otp = generateOtp();
        redisTemplate.opsForValue().set("otp:" + resendCodeDto.getEmail(), otp, 15, TimeUnit.MINUTES);
        emailQueueService.enqueueOtpEmail(resendCodeDto.getEmail(), otp);
    }

    @Override
    public void forgotPasswordRequest(ForgotPasswordDto forgotPasswordDto) {
        validateEmail(forgotPasswordDto.getEmail());

        if (!userRepository.existsByEmail(forgotPasswordDto.getEmail())) {
            throw new RuntimeException("User not found");
        }

        String resetCode = generateOtp();
        redisTemplate.opsForValue().set("reset:" + forgotPasswordDto.getEmail(), resetCode, 15, TimeUnit.MINUTES);
        emailQueueService.enqueuePasswordResetEmail(forgotPasswordDto.getEmail(), resetCode);
    }

    @Override
    public void resetPasswordConfirm(ResetPasswordDto resetPasswordDto) {
        validateEmail(resetPasswordDto.getEmail());
        validatePassword(resetPasswordDto.getNewPassword());

        String storedCode = (String) redisTemplate.opsForValue().get("reset:" + resetPasswordDto.getEmail());
        if (storedCode == null) {
            throw new RuntimeException("Reset code not found or expired");
        }
        if (!storedCode.equals(resetPasswordDto.getResetCode())) {
            throw new RuntimeException("Invalid reset code");
        }

        User user = userRepository.findByEmail(resetPasswordDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userRepository.save(user);

        redisTemplate.delete("reset:" + resetPasswordDto.getEmail());
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {
        validatePassword(changePasswordDto.getNewPassword());

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public AuthResponseDto refresh(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
        String storedToken = (String) redisTemplate.opsForValue().get("refresh:" + email);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("Refresh token not found or revoked");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return createAuthResponse(user);
    }

    @Override
    public void logout(LogoutRequest logoutRequest) {
        String accessToken = logoutRequest.getAccessToken();

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            redisTemplate.opsForValue().set(
                    "blacklist:" + accessToken,
                    "true",
                    jwtTokenProvider.getExpiration(),
                    TimeUnit.MILLISECONDS
            );
        }

        String refreshToken = logoutRequest.getRefreshToken();
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
            redisTemplate.delete("refresh:" + email);
        }
    }

    private AuthResponseDto createAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getRole().name());
        UserDto userDto = UserMapper.toDto(user);

        redisTemplate.opsForValue().set(
                "refresh:" + user.getEmail(),
                refreshToken,
                jwtTokenProvider.getRefreshExpiration(),
                TimeUnit.MILLISECONDS
        );

        AuthResponseDto response = new AuthResponseDto();
        response.setUser(userDto);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        if (!pattern.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }
}
