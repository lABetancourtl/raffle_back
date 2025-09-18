package com.aba.raffle.proyecto.controllers;

import com.aba.raffle.proyecto.dto.LoginDTO;
import com.aba.raffle.proyecto.dto.MensajeDTO;
import com.aba.raffle.proyecto.dto.TokenAndUserDTO;
import com.aba.raffle.proyecto.dto.TokenDTO;
import com.aba.raffle.proyecto.services.impl.LoginServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final LoginServiceImpl loginService;

    @PostMapping("/login")
    public ResponseEntity<MensajeDTO<TokenDTO>> login(@Valid @RequestBody LoginDTO loginDTO) throws Exception {
        TokenDTO tokenDTO = loginService.login(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, tokenDTO));
    }

    @PostMapping("/userlogin")
    public ResponseEntity<MensajeDTO<TokenAndUserDTO>> Userlogin(@Valid @RequestBody LoginDTO loginDTO) throws Exception {
        TokenAndUserDTO tokenDTO = loginService.UserLogin(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, tokenDTO));
    }
}
