package com.planio.app.controllers;

import com.planio.app.dto.AuthDTO;
import com.planio.app.dto.LoginDTO;
import com.planio.app.dto.RegisterDTO;
import com.planio.app.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthDTO register(@RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }

    @PostMapping("/login")
    public AuthDTO login(@RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }
}
