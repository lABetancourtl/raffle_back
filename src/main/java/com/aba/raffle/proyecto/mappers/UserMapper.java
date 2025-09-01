package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.UserCreateDTO;
import com.aba.raffle.proyecto.dto.UserDTO;
import com.aba.raffle.proyecto.model.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(User user); // Convierte User a UserDTO

    @Mapping(target = "id", ignore = true) // ID se genera automáticamente
    @Mapping(target = "estadoUsuario", constant = "INACTIVO")
    @Mapping(target = "role", constant = "ROLE_ADMIN")
    @Mapping(target = "fechaRegistro", expression = "java(java.time.LocalDateTime.now())")
    User fromCreateUserDTO(UserCreateDTO userCreateDTO); // Convierte UserCreateDTO a User
}
