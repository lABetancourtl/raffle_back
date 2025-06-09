package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.services.ImagenService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImagenServiceImpl implements ImagenService {

    private final Cloudinary cloudinary;

    public ImagenServiceImpl(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);

        cloudinary = new Cloudinary(config);
    }

    @Override
    public Map subirImagen(MultipartFile imagen) throws Exception {
        File file = null;
        try {
            file = convertir(imagen);
            return cloudinary.uploader().upload(file, ObjectUtils.asMap("folder", "raffle"));
        } finally {
            if (file != null && file.exists()) {
                file.delete(); // Eliminar archivo temporal después de usarlo
            }
        }
    }

    @Override
    public Map eliminarImagen(String idImagen) throws Exception {
        return cloudinary.uploader().destroy(idImagen, ObjectUtils.emptyMap());
    }

    private File convertir(MultipartFile imagen) throws IOException {
        String originalName = imagen.getOriginalFilename();
        String prefix = (originalName != null && originalName.length() > 3) ? originalName.substring(0, 3) : "img";
        String suffix = (originalName != null && originalName.contains(".")) ? originalName.substring(originalName.lastIndexOf(".")) : null;

        File file = File.createTempFile(prefix, suffix);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(imagen.getBytes());
        fos.close();
        return file;
    }
}
