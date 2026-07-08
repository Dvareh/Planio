package com.planio.app.controllers;

import com.planio.app.dto.ChangePasswordDTO;
import com.planio.app.dto.CurrentUserDTO;
import com.planio.app.dto.UserDTO;
import com.planio.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to users")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Update current user")
    @PutMapping("/me")
    public UserDTO updateMe(
            @RequestBody UserDTO userDTO)
    {
        return userService.updateCurrentUser(userDTO);
    }

    @Operation(summary = "Get current user info")
    @GetMapping("/me")
    public CurrentUserDTO me() {
        return userService.getCurrentUser();
    }

    @Operation(summary = "Change password")
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(changePasswordDTO);

        return ResponseEntity.ok("Password changed successfully");
    }
}