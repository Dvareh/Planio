package com.planio.app.controllers;

import com.planio.app.dto.UserDTO;
import com.planio.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create user")
    @PostMapping("/create")
    public UserDTO create(@RequestBody @Valid UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/get/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @Operation(summary = "Get all users")
    @GetMapping("/get")
    public List<UserDTO> getAll() {
        return userService.getAll();
    }

    @Operation(summary = "Update user")
    @PutMapping("/update/{id}")
    public UserDTO update(@PathVariable Long id,
                          @RequestBody @Valid UserDTO userDTO) {
        return userService.update(id, userDTO);
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}