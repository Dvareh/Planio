package com.planio.app.controllers;

import com.planio.app.dto.UserDTO;
import com.planio.app.entity.Roles;
import com.planio.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin API")
public class AdminController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {

        return userService.getAll();
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/users/{id}")
    public void deleteUser(
            @PathVariable Long id
    ) {

        userService.delete(id);
    }

    @Operation(summary = "Change user role")
    @PutMapping("/users/{id}/role")
    public UserDTO changeRole(
            @PathVariable Long id,
            @RequestParam Roles role
    ) {

        return userService.changeRole(id, role);
    }

}