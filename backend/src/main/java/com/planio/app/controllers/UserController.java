package com.planio.app.controllers;

import com.planio.app.dto.ChangePasswordDTO;
import com.planio.app.dto.CurrentUserDTO;
import com.planio.app.dto.UserDTO;
import com.planio.app.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    @PutMapping("/me")
    public UserDTO updateMe(
            @Valid @RequestBody UserDTO userDTO)
    {
        return userService.updateCurrentUser(userDTO);
    }

    @GetMapping("/me")
    public CurrentUserDTO me() {
        return userService.getCurrentUser();
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(changePasswordDTO);

        return ResponseEntity.ok("Password changed successfully");
    }
}