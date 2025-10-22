package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.LoginDTO;
import com.aba.raffle.proyecto.dto.TokenAndUserDTO;
import com.aba.raffle.proyecto.dto.TokenDTO;
import com.aba.raffle.proyecto.model.entities.User;
import com.aba.raffle.proyecto.model.entities.UserAdmin;
import com.aba.raffle.proyecto.model.enums.EstadoUser;
import com.aba.raffle.proyecto.model.enums.EstadoUsuarioAdmin;
import com.aba.raffle.proyecto.repositories.UserAdminRepository;
import com.aba.raffle.proyecto.repositories.UserRepository;
import com.aba.raffle.proyecto.security.JWTUtils;
import com.aba.raffle.proyecto.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserAdminRepository userAdminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    @Value("${turnstile.secret}")
    private String turnstileSecret;

    @Override
    public TokenDTO login(LoginDTO loginDTO) throws Exception {
        UserAdmin user = userAdminRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new Exception("El usuario no existe"));
        if (!user.getEstadoUsuarioAdmin().equals(EstadoUsuarioAdmin.ACTIVO)) {
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

    @Override
    public TokenAndUserDTO UserLogin(LoginDTO loginDTO) throws Exception {
      // validar turnstile
        if (!validateTurnstile(loginDTO.token())) {
            throw new Exception("Verificación humana fallida");
        }

        // 2️⃣ Validar email y contraseña como antes
        User user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new Exception("El usuario no existe"));

        if (!user.getEstadoUser().equals(EstadoUser.ACTIVO)) {
            throw new Exception("El usuario esta inactivo");
        }

        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new Exception("La contraseña es incorrecta");
        }

        Map<String, String> claims = Map.of(
                "id", user.getId().toString(),
                "email", user.getEmail(),
                "estadoUser", user.getEstadoUser().name()
        );

        String jwtToken = jwtUtils.generateToken(user.getId().toString(), claims);

        return new TokenAndUserDTO(
                jwtToken,
                null, // refreshToken
                user.getName(),
                user.getSurName(),
                user.getEmail()
        );
    }

    private boolean validateTurnstile(String token) {
        String secretKey = turnstileSecret;
        String url = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", token);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, params, Map.class);
            return response != null && Boolean.TRUE.equals(response.get("success"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
