package com.planio.app.dto;

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
}
