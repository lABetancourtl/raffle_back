package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.UserAdminCreateDTO;
import com.aba.raffle.proyecto.mappers.UserAdminMapper;
import com.aba.raffle.proyecto.model.entities.User;
import com.aba.raffle.proyecto.model.entities.UserAdmin;
import com.aba.raffle.proyecto.model.enums.EstadoUsuarioAdmin;
import com.aba.raffle.proyecto.repositories.UserAdminRepository;
import com.aba.raffle.proyecto.repositories.UserRepository;
import com.aba.raffle.proyecto.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserAdminRepository userAdminRepository;
    private final UserRepository userRepository;

    private final UserAdminMapper userAdminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void crearUserAdmin(UserAdminCreateDTO userAdminCreateDTO) throws Exception {
        if(existeEmailAdmin(userAdminCreateDTO.email())){
            throw new Exception("El email ya existe");
        }
        UserAdmin user = userAdminMapper.fromCreateUserDTO(userAdminCreateDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userAdminRepository.save(user);

    }

    @Override
    public void crearUser(UserAdminCreateDTO userCreate) throws Exception {
        if(existeEmailAdmin(userCreate.email())){
            throw new Exception("Ya existe una cuenta activa con este email o esta en proceso de ser activada");
        }
        User user =

    }

    @Override
    public void activarUser(Long id) {
        UserAdmin user = userAdminRepository.findById(id).orElseThrow();
        user.setEstadoUsuarioAdmin(EstadoUsuarioAdmin.ACTIVO);
        userAdminRepository.save(user);
    }

    @Override
    public void desactivarUsuer(Long id) {
        UserAdmin user = userAdminRepository.findById(id).orElseThrow();
        user.setEstadoUsuarioAdmin(EstadoUsuarioAdmin.INACTIVO);
        userAdminRepository.save(user);

    }

    private boolean existeEmailAdmin(String email) {
        return userAdminRepository.findByEmail(email).isPresent();
    }

    private boolean existeEmailUser(String email) {
        return userRepository.findByUserEmail(email).isPresent();
    }
}
