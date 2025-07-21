package com.example.userapi.dto;

import com.example.userapi.model.User;
import com.example.userapi.model.Role;

import java.util.stream.Collectors;

public class UserResponseMapper {

    public static UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setPassword(user.getPassword());
        userResponseDTO.setFirstname(user.getFirstname());
        userResponseDTO.setLastname(user.getLastname());
        userResponseDTO.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        return userResponseDTO;
    }
}
