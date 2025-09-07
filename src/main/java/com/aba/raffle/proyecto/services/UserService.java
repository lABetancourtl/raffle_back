package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.ActivarCuentaDTO;
import com.aba.raffle.proyecto.dto.UserAdminCreateDTO;
import com.aba.raffle.proyecto.dto.UserNotValidatedCreateDTO;
import com.aba.raffle.proyecto.model.vo.CodigoValidacion;
import jakarta.validation.Valid;

public interface UserService {

    void crearUserAdmin(UserAdminCreateDTO userCreate) throws Exception;

    void crearUser(UserNotValidatedCreateDTO userNotValidatedCreate) throws Exception;

    void activarUser(@Valid Long id) throws Exception;


    void desactivarUsuer(@Valid Long id) throws Exception;

    void validarEmail(ActivarCuentaDTO activarCuentaDTO) throws Exception;

}
