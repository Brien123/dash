package com.example.dash.user.controller;

import com.example.dash.authentication.dto.RegisterDto;
import com.example.dash.common.dto.ApiResponse;
import com.example.dash.user.dto.UserDto;
import com.example.dash.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/v1")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long userId){
        return ResponseEntity.ok(ApiResponse.success("User gotten successfully", userService.getUser(userId)));
    }

    @PatchMapping("/")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@RequestBody RegisterDto registerDto){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success("Details updated successfully.", userService.updateUser(registerDto, email)));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDto>>> listUsers(@PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable){
        return ResponseEntity.ok(ApiResponse.success("Users listed successfully", userService.listUsers(pageable)));
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeUserStatus(@PathVariable Long userId){
        userService.changeUserStatus(userId);
        return ResponseEntity.ok(ApiResponse.success("User status changed successfully"));
    }

}
