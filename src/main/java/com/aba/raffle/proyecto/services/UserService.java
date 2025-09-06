package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.UserAdminCreateDTO;
import jakarta.validation.Valid;

public interface UserService {

    void crearUserAdmin(UserAdminCreateDTO userCreate) throws Exception;

    void crearUser(@Valid UserAdminCreateDTO userCreate) throws Exception;

    void activarUser(@Valid Long id) throws Exception;


    void desactivarUsuer(@Valid Long id) throws Exception;

}
