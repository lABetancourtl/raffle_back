package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.LoginDTO;
import com.aba.raffle.proyecto.dto.TokenDTO;
import com.aba.raffle.proyecto.model.entities.User;
import com.aba.raffle.proyecto.model.enums.EstadoUsuario;
import com.aba.raffle.proyecto.repositories.UserRepository;
import com.aba.raffle.proyecto.security.JWTUtils;
import com.aba.raffle.proyecto.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    @Override
    public TokenDTO login(LoginDTO loginDTO) throws Exception {
        User user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new Exception("El usuario no existe"));
        if (!user.getEstadoUsuario().equals(EstadoUsuario.ACTIVO)) {
            throw new Exception("El usuario esta inactivo");
        }

        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new Exception("La contraseña es incorrecta");
        }

        Map<String, String> claims = Map.of(
                "id", user.getId().toString(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        );
        String jwtToken = jwtUtils.generateToken(user.getId().toString(), claims);
        return new TokenDTO(jwtToken, null);
    }
}
