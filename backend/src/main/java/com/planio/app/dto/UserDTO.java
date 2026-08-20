package com.planio.app.dto;

import com.planio.app.entity.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "Username should not be blank")
    private String username;

    @Email
    @NotBlank
    private String email;

    private Roles role;
}
