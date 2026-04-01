package com.example.pacientesjavafxequipo12.services;

import com.example.pacientesjavafxequipo12.models.Paciente;
import com.example.pacientesjavafxequipo12.repositories.PacienteRepository;
import java.io.IOException;
import java.util.*;

public class PacienteService {
    private PacienteRepository repo = new PacienteRepository();

    public List<Paciente> obtenerPacientes() throws IOException {
        List<String> lineas = repo.leerTodo();
        List<Paciente> lista = new ArrayList<>();
        for (String l : lineas) {
            if (l.isBlank()) continue;
            String[] p = l.split(",");
            lista.add(new Paciente(p[0], p[1], Integer.parseInt(p[2]), p[3], p[4], p[5]));
        }
        return lista;
    }

    public void validar(Paciente p, List<Paciente> actuales, boolean esNuevo) {
        if (p.getCurp().isBlank() || p.getNombre().length() < 5)
            throw new IllegalArgumentException("Datos inválidos o nombre muy corto");
        if (p.getEdad() < 0 || p.getEdad() > 120)
            throw new IllegalArgumentException("Edad fuera de rango");
        if (!p.getTelefono().matches("\\d{10}"))
            throw new IllegalArgumentException("Teléfono debe ser de 10 dígitos");

        if (esNuevo) {
            for (Paciente a : actuales) {
                if (a.getCurp().equalsIgnoreCase(p.getCurp()))
                    throw new IllegalArgumentException("La CURP ya existe");
            }
        }
    }

    public void guardarCambios(List<Paciente> lista) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Paciente p : lista) lineas.add(p.toString());
        repo.guardarTodo(lineas);
    }
}