package com.aba.raffle.proyecto.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImagenService {
    Map subirImagen(MultipartFile imagen) throws Exception;
    Map eliminarImagen(String idImagen) throws Exception;
}
