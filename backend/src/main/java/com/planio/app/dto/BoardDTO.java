package com.planio.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardDTO {

    private Long id;

    @NotBlank
    private String name;

    private Long ownerId;
}
