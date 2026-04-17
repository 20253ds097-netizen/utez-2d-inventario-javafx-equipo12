package com.example.pacientesjavafxequipo12.repositories;

import com.example.pacientesjavafxequipo12.utils.Paths;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class PacienteRepository {


    public PacienteRepository() {
        try {
            // Especificamos java.nio.file.Paths para que no use tu clase utils.Paths
            Path dirPath = java.nio.file.Paths.get("data");
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = java.nio.file.Paths.get(Paths.NOMBRE_ARCHIVO);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.err.println("Error al inicializar el almacenamiento: " + e.getMessage());
        }
    }

    public List<String> readAllLines() throws IOException {
        Path path = java.nio.file.Paths.get(Paths.NOMBRE_ARCHIVO);
        if (!Files.exists(path)) return List.of();
        return Files.readAllLines(path);
    }

    public void saveAllLines(List<String> lines) throws IOException {
        // StandardOpenOption.CREATE y TRUNCATE_EXISTING aseguran que el archivo se limpie y se escriba lo nuevo
        Path path = java.nio.file.Paths.get(Paths.NOMBRE_ARCHIVO);
        Files.write(path, lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}