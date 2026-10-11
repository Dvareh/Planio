package com.planio.app.dto;

import com.planio.app.entity.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "Username should not be blank")
    @Size(max = 255, message = "Username cannot exceed 255 characters")
    private String username;


    @Email
    @NotBlank
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    private Roles role;
}
