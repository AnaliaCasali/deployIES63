package com.backend.ISP63.controller;

import com.backend.ISP63.dto.ImagenDTO;
import com.backend.ISP63.repository.ImagenRepository;
import com.backend.ISP63.service.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/auth/imagenes")

public class ImagenAuthController {

    @Autowired
    ImagenService imagenService;

    @Autowired
    ImagenRepository imagenRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping()
    public ResponseEntity<List<ImagenDTO>> getAll() {
        try {
            List<ImagenDTO> response = imagenService.getAll();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagenDTO> getById(@PathVariable int id) throws Exception {
        ImagenDTO response = imagenService.findById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/ImageAsResource/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getImageAsResource(@PathVariable String filename) {
        Path file = Paths.get(filename).resolve(filename);
        Resource resource = null;
        try {
            resource = new UrlResource(file.toUri());
        } catch (MalformedURLException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @GetMapping("/archivo/{id}")
    public ResponseEntity<byte[]> getImagenFile(@PathVariable Integer id) {
        ImagenDTO imagen = null;
        try {
            imagen = imagenService.findById(id);
            File file = new File(imagen.getUrl().replace("/uploads/", uploadPath)); // Reemplaza '/uploads/' con el path en el servidor
            byte[] bytes = null;
            bytes = Files.readAllBytes(file.toPath());
            String contentType = "image/jpeg"; // Cambia esto según el tipo de imagen si es necesario
            if (imagen.getNombre().endsWith(".png")) {
                contentType = "image/png";
            } else if (imagen.getNombre().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (imagen.getNombre().endsWith(".jpg")) {
                contentType = "image/jpg";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(bytes);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @GetMapping("/archivoResource/{id}")
    public ResponseEntity<Resource> getImagenResource(@PathVariable Integer id) {
        try {
            // Obtener la imagen del servicio
            ImagenDTO imagen = imagenService.findById(id);

            File file = new File(imagen.getUrl().replace("/uploads/", uploadPath)); // Reemplaza '/uploads/' con el path en el servidor

            // Leer los bytes del archivo
            byte[] bytes = Files.readAllBytes(file.toPath());

            // Crear un recurso de tipo ByteArrayResource para devolver los bytes como recurso
            Resource resource = new ByteArrayResource(bytes);

            // Determinar el tipo de contenido de la imagen
            String contentType = Files.probeContentType(file.toPath()); // Determina el tipo automáticamente
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback a binario genérico si no se detecta el tipo
            }

            // Crear cabeceras para la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(bytes.length);
            headers.setContentDispositionFormData("attachment", imagen.getNombre());

            // Devolver el archivo como respuesta
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Error al encontrar o leer el archivo
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Otro tipo de error
        }
    }


}
