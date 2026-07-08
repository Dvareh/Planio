package com.planio.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentUserDTO {

    private Long id;

    private String name;

    private String email;

    private String role;
}
