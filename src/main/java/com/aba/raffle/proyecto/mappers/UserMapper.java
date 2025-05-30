package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.UserCreateDTO;
import com.aba.raffle.proyecto.dto.UserDTO;
import com.aba.raffle.proyecto.model.documents.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(User user); //convierte User a UserDTO

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estadoUsuario", constant = "INACTIVO")
    @Mapping(target = "role", constant = "ROLE_ADMIN")
    @Mapping(target = "fechaRegistro", expression = "java(java.time.LocalDate.now().toString())")
    User fromCreateUserDTO(UserCreateDTO userCreateDTO); //convierte UserCreateDTO a User
}
