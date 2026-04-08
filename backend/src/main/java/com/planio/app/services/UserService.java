package com.planio.app.services;

import com.planio.app.dto.UserDTO;
import com.planio.app.entity.User;
import com.planio.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
        log.info("Creating user with email: {}", userDTO.getEmail());
        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password("temp")
                .build();

        User saved = userRepository.save(user);

        log.info("User created with id: {}", saved.getId());

        return mapToDTO(saved);
    }

    public UserDTO getById(Long id) {
        log.info("Fetching user by id: {}", id);
        return mapToDTO(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public List<UserDTO> getAll() {
        log.info("Fetching all users");
        return userRepository.findAll().
                stream().
                map(this::mapToDTO)
                .toList();
    }

    public UserDTO update(Long id, UserDTO userDTO) {
        log.info("Updating user id: {}", id);

        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        return mapToDTO(userRepository.save(user));
    }

    public void delete(Long id) {
        log.warn("Deleting user id: {}", id);
        userRepository.deleteById(id);
    }
}
