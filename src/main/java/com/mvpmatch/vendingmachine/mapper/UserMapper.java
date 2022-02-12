package com.mvpmatch.vendingmachine.mapper;

import com.mvpmatch.vendingmachine.domain.Role;
import com.mvpmatch.vendingmachine.domain.User;
import com.mvpmatch.vendingmachine.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRoles(user.getRoles()
                .stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet())
        );

        return userDTO;
    }
}
