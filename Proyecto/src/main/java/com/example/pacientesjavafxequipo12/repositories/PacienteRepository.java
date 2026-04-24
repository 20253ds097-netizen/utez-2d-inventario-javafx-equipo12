package com.example.pacientesjavafxequipo12.repositories;

import com.example.pacientesjavafxequipo12.utils.Paths;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class PacienteRepository {

    public PacienteRepository() {
        try {
            // Especificamos java.nio.file.Paths para que no use la clase utils.Paths
            Path dirPath = java.nio.file.Paths.get("data");
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = java.nio.file.Paths.get(Paths.NOMBRE_ARCHIVO);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.out.println("Error al inicializar el almacenamiento: " + e.getMessage());
        }
    }
}