package com.aba.raffle.proyecto.dto;

import com.aba.raffle.proyecto.model.enums.EstadoUsuario;
import com.aba.raffle.proyecto.model.enums.Role;

public record UserDTO(
        Long id,
        String name,
        String email,
        Role role,
        EstadoUsuario estadoUsuario,
        String fechaRegistro
) {
}
