package com.planio.app.controllers;

import com.planio.app.dto.UserDTO;
import com.planio.app.entity.Roles;
import com.planio.app.services.AdminService;
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
    private final AdminService adminService;

    @Operation(summary = "Get all users")
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAll();
    }

    @Operation(summary = "Change user role")
    @PutMapping("/users/{id}/role")
    public void changeRole(@PathVariable Long id, @RequestParam Roles role
    ) {
        adminService.updateRole(id, role);
    }

}