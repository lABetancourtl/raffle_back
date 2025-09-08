package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.UserNotValidatedCreateDTO;
import com.aba.raffle.proyecto.model.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true) //Id se genera automaticamente
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "surName", ignore = true)
    @Mapping(target = "DocNumber", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "estadoUser", constant = "PENDIENTE_EMAIL")
    @Mapping(target = "codigoValidacion", ignore = true)
    User fromCreateUserNotValidatedDTO(UserNotValidatedCreateDTO  userNotValidatedCreateDTO); //Convierte UserNotValidatedCreateDTO a User

    //    @Mapping(target = "role", constant = "ROLE_ADMIN")
}
