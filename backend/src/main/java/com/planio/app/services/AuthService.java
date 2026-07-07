package com.planio.app.services;

import com.planio.app.dto.AuthDTO;
import com.planio.app.dto.LoginDTO;
import com.planio.app.dto.RegisterDTO;
import com.planio.app.entity.Roles;
import com.planio.app.entity.User;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthDTO register(RegisterDTO registerDTO) {

        User user = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(Roles.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthDTO(token);
    }

    public AuthDTO login(LoginDTO loginDTO) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ObjectNotFoundException("User", loginDTO.getEmail()));

        String token = jwtService.generateToken(user);

        return new AuthDTO(token);
    }
}
