package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.UserCreateDTO;
import jakarta.validation.Valid;

public interface UserService {

    void crearUser(UserCreateDTO userCreate) throws Exception;

    void activarUser(@Valid Long id) throws Exception;


    void desactivarUsuer(@Valid Long id) throws Exception;


}
