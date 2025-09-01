package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.UserCreateDTO;
import com.aba.raffle.proyecto.mappers.UserMapper;
import com.aba.raffle.proyecto.model.entities.User;
import com.aba.raffle.proyecto.model.enums.EstadoUsuario;
import com.aba.raffle.proyecto.repositories.UserRepository;
import com.aba.raffle.proyecto.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void crearUser(UserCreateDTO userCreateDTO) throws Exception {
        if(existeEmail(userCreateDTO.email())){
            throw new Exception("El email ya existe");
        }
        User user = userMapper.fromCreateUserDTO(userCreateDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

    }

    @Override
    public void activarUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEstadoUsuario(EstadoUsuario.ACTIVO);
        userRepository.save(user);
    }

    @Override
    public void desactivarUsuer(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEstadoUsuario(EstadoUsuario.INACTIVO);
        userRepository.save(user);

    }

    private boolean existeEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
