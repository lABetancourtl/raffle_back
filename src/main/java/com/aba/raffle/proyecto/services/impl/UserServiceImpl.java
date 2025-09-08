package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.ActivarCuentaDTO;
import com.aba.raffle.proyecto.dto.UserAdminCreateDTO;
import com.aba.raffle.proyecto.dto.UserNotValidatedCreateDTO;
import com.aba.raffle.proyecto.dto.UserValidatedCreateDTO;
import com.aba.raffle.proyecto.mappers.UserAdminMapper;
import com.aba.raffle.proyecto.mappers.UserMapper;
import com.aba.raffle.proyecto.model.entities.User;
import com.aba.raffle.proyecto.model.entities.UserAdmin;
import com.aba.raffle.proyecto.model.enums.EstadoUser;
import com.aba.raffle.proyecto.model.enums.EstadoUsuarioAdmin;
import com.aba.raffle.proyecto.model.vo.CodigoValidacion;
import com.aba.raffle.proyecto.repositories.UserAdminRepository;
import com.aba.raffle.proyecto.repositories.UserRepository;
import com.aba.raffle.proyecto.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        userAdminRepository.save(userAdmin);

    }

    @Override
    public void validarDocumento(UserValidatedCreateDTO userValidatedCreateDTO) {
        User user = userRepository.findByEmail(userValidatedCreateDTO.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + userValidatedCreateDTO.email()));

        user.setName(userValidatedCreateDTO.name());
        user.setSurName(userValidatedCreateDTO.surName());
        user.setDocNumber(userValidatedCreateDTO.DocNumber());
        user.setDateOfBirth(userValidatedCreateDTO.dateOfBirth());
        user.setEstadoUser(EstadoUser.ACTIVO);

        userRepository.save(user);
    }

    //Metodo para crear usuario final (home)
    @Override
    public void crearUser(UserNotValidatedCreateDTO userNotValidatedCreateDTO) throws Exception {
        if(existeEmailUser(userNotValidatedCreateDTO.email())){
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

    @Override
    public void validarEmail(ActivarCuentaDTO activarCuentaDTO) throws Exception  {
        User user = obtenerPorEmail(activarCuentaDTO.email());
        if (!user.getCodigoValidacion().getCodigo().equals(activarCuentaDTO.codigoValidacion())) {
            throw new Exception("El código de verificación es incorrecto");
        }
        if(!LocalDateTime.now().isBefore(user.getCodigoValidacion().getFechaCreacion().plusMinutes(15))) {
            throw new Exception("El código de verificación ha caducado");
        }
        user.setEstadoUser(EstadoUser.PENDIENTE_VERIFICACION);
        user.setCodigoValidacion(null);
        userRepository.save(user);
    }

    @Override
    public List<User> obtenerUsuarioEmailVerificado() {
        return userRepository.findByEstadoUser(EstadoUser.PENDIENTE_VERIFICACION);
    }




    //Metodo usado en activarCuenta y cambiarPassword para obtener el usuario por email
    private User obtenerPorEmail(String email) throws Exception{
        Optional<User> usuarioOptional = userRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            throw new Exception("No se encontró el usuario con el email " + email);
        }
        return usuarioOptional.get();
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
