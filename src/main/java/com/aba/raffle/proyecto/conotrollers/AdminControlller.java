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

    @PatchMapping("/activarUsuario/{id}")
    public ResponseEntity<MensajeDTO<String>> activarUsuario(@Valid @PathVariable String id) throws Exception{
        userService.activarUser(id);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Usuario activado correctamente"));
    }

    @PatchMapping("/desactivarUsuario/{id}")
    public ResponseEntity<MensajeDTO<String>> desactivarUsuario(@Valid @PathVariable String id) throws Exception {
        userService.desactivarUsuer(id);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Usuario desactivado correctamente"));
    }

}
