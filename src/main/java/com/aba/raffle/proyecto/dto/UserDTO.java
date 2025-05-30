package com.aba.raffle.proyecto.dto;

import com.aba.raffle.proyecto.model.enums.EstadoUsuario;
import com.aba.raffle.proyecto.model.enums.Role;
import org.bson.types.ObjectId;

public record UserDTO(
        ObjectId id,
        String name,
        String email,
        Role role,
        EstadoUsuario estadoUsuario,
        String fechaRegistro
) {
}
