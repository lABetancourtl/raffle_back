package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.UserAdminCreateDTO;
import com.aba.raffle.proyecto.dto.UserNotValidatedCreateDTO;
import com.aba.raffle.proyecto.mappers.UserAdminMapper;
import com.aba.raffle.proyecto.mappers.UserMapper;
import com.aba.raffle.proyecto.model.entities.User;
import com.aba.raffle.proyecto.model.entities.UserAdmin;
import com.aba.raffle.proyecto.model.enums.EstadoUsuarioAdmin;
import com.aba.raffle.proyecto.model.vo.CodigoValidacion;
import com.aba.raffle.proyecto.repositories.UserAdminRepository;
import com.aba.raffle.proyecto.repositories.UserRepository;
import com.aba.raffle.proyecto.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserAdminRepository userAdminRepository;
    private final UserRepository userRepository;

    private final UserAdminMapper userAdminMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    //Metodo para crear usuario admin
    @Override
    public void crearUserAdmin(UserAdminCreateDTO userAdminCreateDTO) throws Exception {
        if(existeEmailAdmin(userAdminCreateDTO.email())){
            throw new Exception("El email ya existe");
        }
        UserAdmin userAdmin = userAdminMapper.fromCreateUserDTO(userAdminCreateDTO);
        userAdmin.setPassword(passwordEncoder.encode(userAdmin.getPassword()));

        System.out.println("Datos del usuario a crear:" + userAdmin);
        userAdminRepository.save(userAdmin);

    }

    //Metodo para crear usuario final (home)
    @Override
    public void crearUser(UserNotValidatedCreateDTO userNotValidatedCreateDTO) throws Exception {
        if(existeEmailAdmin(userNotValidatedCreateDTO.email())){
            throw new Exception("Ya existe una cuenta activa con este email o esta en proceso de ser activada");
        }
        User userHome = userMapper.fromCreateUserNotValidatedDTO(userNotValidatedCreateDTO);
        userHome.setPassword(passwordEncoder.encode(userHome.getPassword()));

        String codigoActivacion = generarCodigo();
        userHome.setCodigoValidacion(new CodigoValidacion(
                codigoActivacion,
                LocalDateTime.now()
        ));

        userRepository.save(userHome);

        String asunto = "Verificacion de cuenta";
        String destinatario = userHome.getEmail();
        emailService.sendEmailCode(destinatario, asunto, codigoActivacion);

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
        return userRepository.findByEmail(email).isPresent();
    }


    private String generarCodigo() {
        String digitos = "0123456789";
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int indice = (int) (Math.random() * digitos.length());
            codigo.append(digitos.charAt(indice));
        }
        return codigo.toString();
    }
}
