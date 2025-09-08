package com.aba.raffle.proyecto.controllers;


import com.aba.raffle.proyecto.dto.*;
import com.aba.raffle.proyecto.model.entities.User;
import com.aba.raffle.proyecto.services.PurchaseService;
import com.aba.raffle.proyecto.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final PurchaseService purchaseService;


    @PostMapping("/crearUsuario")     //Este endpoint es para crear cuentas de administrador
    public ResponseEntity<MensajeDTO<String>> crearUserAdmin(@Valid @RequestBody UserAdminCreateDTO userCreate) throws Exception{
        userService.crearUserAdmin(userCreate);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Usuario creado correctamente"));
    }

    @PostMapping("/crearUsuarioHome") //Este endpoint es para crear cuenta de usuario final
    public ResponseEntity<MensajeDTO<String>> crearUsuarioHome(@Valid @RequestBody UserNotValidatedCreateDTO userCreate) throws Exception{
        userService.crearUser(userCreate);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Usuario creado correctamente"));
    }

    @GetMapping("/usuarioEmailVerificado")
    public ResponseEntity<List<User>> usuarioEmailVerificado() {
        List<User> users = userService.obtenerUsuarioEmailVerificado();
        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay usuarios con email verificado");
        }
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/validarEmail")
    public ResponseEntity<MensajeDTO<String>> validarEmail(@RequestBody ActivarCuentaDTO activarCuentaDTO) throws Exception {
        System.out.println("Datos que llegan del front " + activarCuentaDTO);
        userService.validarEmail(activarCuentaDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Activado correctamente."));
    }

    @PatchMapping("/validarDocumento")
    public ResponseEntity<MensajeDTO<String>> validarDocumento(@RequestBody UserValidatedCreateDTO userValidatedCreateDTO) throws Exception {
        userService.validarDocumento(userValidatedCreateDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Activado correctamente."));
    }

    @PatchMapping("/activarUsuario/{id}")
    public ResponseEntity<MensajeDTO<String>> activarUsuario(@Valid @PathVariable Long id) throws Exception{
        userService.activarUser(id);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Usuario activado correctamente"));
    }

    @PatchMapping("/desactivarUsuario/{id}")
    public ResponseEntity<MensajeDTO<String>> desactivarUsuario(@Valid @PathVariable Long id) throws Exception {
        userService.desactivarUsuer(id);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Usuario desactivado correctamente"));
    }

    @PostMapping("/asignarAleatorio")
    public ResponseEntity<MensajeDTO<List<NumeroDTO>>> asignarNumerosAleatoriosDesdeAdmin(@RequestBody BuyRequestDTO buyRequestDTO) {
        List<NumeroDTO> randomNumbers = purchaseService.asignarNumerosAleatoriosDesdeAdmin(buyRequestDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false,  randomNumbers));
    }


    @PostMapping("/asignarNumero")
    public ResponseEntity<MensajeDTO<NumeroDTO>> asignarNumero(@Valid @RequestBody BuyRequestDTO buyRequestDTO, NumeroDTO numeroDTO) {
        System.out.println("Numero que llega: " + numeroDTO);
        NumeroDTO numeroAsignado = purchaseService.asignarNumeroDesdeAdmin(buyRequestDTO, numeroDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false,  numeroAsignado));
    }



}
