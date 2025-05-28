package com.aba.raffle.proyecto.conotrollers;


import com.aba.raffle.proyecto.dto.MensajeDTO;
import com.aba.raffle.proyecto.dto.UserCreateDTO;
import com.aba.raffle.proyecto.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminControlller {

    private final UserService userService;


    @PostMapping("/crearUsuario")
    public ResponseEntity<MensajeDTO<String>> crearUsuario(@Valid @RequestBody UserCreateDTO userCreate) throws Exception{
        userService.crearUser(userCreate);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Usuario creado correctamente"));
    }

}
