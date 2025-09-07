package com.aba.raffle.proyecto.controllers;

import com.aba.raffle.proyecto.services.ImagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService imagenService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> subirImagen(@RequestParam("imagen") MultipartFile imagen) throws Exception {
        Map datos = imagenService.subirImagen(imagen);
        String url = (String) datos.get("secure_url");

        // Devuelve JSON explícito
        return ResponseEntity.ok(Map.of("url", url));
    }





}