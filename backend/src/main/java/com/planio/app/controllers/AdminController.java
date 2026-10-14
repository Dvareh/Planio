package com.planio.app.controllers;

import com.planio.app.dto.UserDTO;
import com.planio.app.entity.Roles;
import com.planio.app.services.AdminService;
import com.planio.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAll();
    }

    @PutMapping("/users/{id}/role")
    public void changeRole(@PathVariable Long id, @RequestParam Roles role
    ) {
        adminService.updateRole(id, role);
    }

}