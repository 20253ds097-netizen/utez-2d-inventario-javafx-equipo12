package com.example.pacientesjavafxequipo.service;
import com.example.pacientesjavafxequipo.models.Paciente;
import com.example.pacientesjavafxequipo.repositories.PacienteRepository;
import java.io.IOException;
import java.util.*;

public class PacienteService {
    private PacienteRepository repo = new PacienteRepository();

    public List<Paciente> obtenerPacientes() throws IOException {
        List<String> lineas = repo.readAllLines();
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
        repo.saveAllLines(lineas);
    }


    public void agregarPaciente(Paciente nuevo) throws IOException {
        List<Paciente> actuales = obtenerPacientes();
        validar(nuevo, actuales, true);
        actuales.add(nuevo);
        guardarCambios(actuales);
    }

    public void cambiarEstatus(String curp) throws IOException {
        List<Paciente> lista = obtenerPacientes();
        for (Paciente p : lista) {
            if (p.getCurp().equalsIgnoreCase(curp)) {
                String nuevoEstatus = p.getEstatus().equalsIgnoreCase("activo") ? "inactivo" : "activo";
                p.setEstatus(nuevoEstatus);
                break;
            }
        }
        guardarCambios(lista);
    }

    public long totalActivos() throws IOException {
        int count = 0;
        for (Paciente p : obtenerPacientes())
            if (p.getEstatus().equalsIgnoreCase("activo")) count++;
        return count;
    }

    public long totalInactivos() throws IOException {
        int count = 0;
        for (Paciente p : obtenerPacientes())
            if (p.getEstatus().equalsIgnoreCase("inactivo")) count++;
        return count;
    }
}