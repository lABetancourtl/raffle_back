package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.LoginDTO;
import com.aba.raffle.proyecto.dto.TokenDTO;
import jakarta.validation.Valid;

public interface LoginService {
    TokenDTO login(@Valid LoginDTO loginDTO) throws Exception;

    TokenDTO UserLogin(@Valid LoginDTO loginDTO) throws Exception;
}
