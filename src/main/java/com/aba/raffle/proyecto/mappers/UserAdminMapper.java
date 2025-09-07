package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.UserAdminCreateDTO;
import com.aba.raffle.proyecto.dto.UserDTO;
import com.aba.raffle.proyecto.model.entities.UserAdmin;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserAdminMapper {

    UserDTO toUserDTO(UserAdmin user); // Convierte User a UserDTO

    @Mapping(target = "id", ignore = true) // ID se genera automáticamente
    @Mapping(target = "estadoUsuarioAdmin", constant = "INACTIVO")
    @Mapping(target = "role", constant = "ROLE_ADMIN")
    @Mapping(target = "fechaRegistro", expression = "java(java.time.LocalDateTime.now())")
    UserAdmin fromCreateUserDTO(UserAdminCreateDTO userAdminCreateDTO); // Convierte UserCreateDTO a User
}
