package com.planio.app.services;

import com.planio.app.dto.AuthDTO;
import com.planio.app.dto.LoginDTO;
import com.planio.app.dto.RegisterDTO;
import com.planio.app.entity.Roles;
import com.planio.app.entity.User;
import com.planio.app.exceptions.ObjectNotFoundException;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthDTO register(RegisterDTO registerDTO) {
        String email = validateEmailCase(registerDTO.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .username(registerDTO.getUsername())
                .email(email)
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(Roles.USER)
                .build();

        userRepository.save(user);

        return new AuthDTO(jwtService.generateToken(user));
    }

    public AuthDTO login(LoginDTO loginDTO) {
        String email = validateEmailCase(loginDTO.getEmail());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, loginDTO.getPassword()));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ObjectNotFoundException("User", email));

        return new AuthDTO(jwtService.generateToken(user));
    }

    private String validateEmailCase(String email) {
        if (!email.equals(email.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email must be lowercase"
            );
        }

        return email;
    }
}
