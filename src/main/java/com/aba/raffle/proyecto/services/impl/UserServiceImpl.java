package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.UserCreateDTO;
import com.aba.raffle.proyecto.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Override
    public void crearUser(UserCreateDTO userCreate) throws Exception {
        System.out.println("funcionando");

    }
}
