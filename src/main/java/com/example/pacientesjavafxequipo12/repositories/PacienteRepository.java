package com.example.pacientesjavafxequipo12.repositories;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class PacienteRepository {
    private final Path filePath = Paths.get("data", "pacientes.csv");

    public PacienteRepository() {
        try {
            if (Files.notExists(filePath.getParent())) Files.createDirectories(filePath.getParent());
            if (Files.notExists(filePath)) Files.createFile(filePath);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<String> leerTodo() throws IOException {
        return Files.readAllLines(filePath, StandardCharsets.UTF_8);
    }

    public void guardarTodo(List<String> lineas) throws IOException {
        Files.write(filePath, lineas, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }
}