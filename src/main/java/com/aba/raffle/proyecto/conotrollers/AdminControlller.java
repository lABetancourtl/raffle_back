package com.aba.raffle.proyecto.conotrollers;


import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.dto.MensajeDTO;
import com.aba.raffle.proyecto.dto.NumeroDTO;
import com.aba.raffle.proyecto.dto.UserAdminCreateDTO;
import com.aba.raffle.proyecto.services.PurchaseService;
import com.aba.raffle.proyecto.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminControlller {

    private final UserService userService;
    private final PurchaseService purchaseService;

    //Este endpoint es para crear cuentas de administrador
    @PostMapping("/crearUsuario")
    public ResponseEntity<MensajeDTO<String>> crearUsuario(@Valid @RequestBody UserAdminCreateDTO userCreate) throws Exception{
        userService.crearUserAdmin(userCreate);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Usuario creado correctamente"));
    }

    @PostMapping("/crearUsuarioHome")
    public ResponseEntity<MensajeDTO<String>> crearUsuarioHome(@Valid @RequestBody UserAdminCreateDTO userCreate) throws Exception{
        userService.crearUser(userCreate);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Usuario creado correctamente"));
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
