package com.planio.app.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {

    private String oldPassword;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
}
