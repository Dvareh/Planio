package com.planio.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank
    @Size(max = 255, message = "Username cannot exceed 255 characters")
    private String username;

    @Email
    @NotBlank
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
