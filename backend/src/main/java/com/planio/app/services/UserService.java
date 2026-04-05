package com.planio.app.services;

import com.planio.app.dto.UserDTO;
import com.planio.app.entity.User;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password("temp")
                .build();

        return mapToDTO(userRepository.save(user));
    }
}
