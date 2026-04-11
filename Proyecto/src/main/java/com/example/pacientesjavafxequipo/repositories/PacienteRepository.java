package com.example.pacientesjavafxequipo.repositories;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;


public class PacienteRepository {
 private final String FILE_PATH = "data/pacientes.csv";

    public PacienteRepository() {
         try {
            Files.createDirectories(Paths.get("data"));
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            System.out.println("Error al crear el archivo de base de datos.");
        }
    }

    public List<String> readAllLines() throws IOException {
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) return List.of();
        return Files.readAllLines(path);
    }

    public void saveAllLines(List<String> lines) throws IOException {
        Files.write(Paths.get(FILE_PATH), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
