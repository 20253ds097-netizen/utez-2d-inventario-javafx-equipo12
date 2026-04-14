package com.example.pacientesjavafxequipo.service;
<<<<<<< HEAD
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
=======

import com.example.pacientesjavafxequipo.models.Paciente;
import com.example.pacientesjavafxequipo.repositories.PacienteRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PacienteService {

    private final PacienteRepository repo = new PacienteRepository();

    // ── Cargar pacientes desde archivo ────────────────────────────
    public List<Paciente> obtenerPacientes() throws IOException {
        List<String> lineas = repo.readAllLines();
        List<Paciente> lista = new ArrayList<>();

        for (String linea : lineas) {
            if (linea == null || linea.isBlank()) continue;

            String[] p = linea.split(",", -1);

            if (p.length != 6) {
                System.err.println("Línea ignorada (formato inválido): " + linea);
                continue;
            }

            try {
                lista.add(new Paciente(
                        p[0].trim(),
                        p[1].trim(),
                        Integer.parseInt(p[2].trim()),
                        p[3].trim(),
                        p[4].trim(),
                        p[5].trim()
                ));
            } catch (NumberFormatException e) {
                System.err.println("Edad inválida en línea: " + linea);
            }
>>>>>>> dev
        }
        return lista;
    }

<<<<<<< HEAD
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
=======
    // ── Alias para compatibilidad con MainController ──────────────
    public List<Paciente> loadPacientes() throws IOException {
        return obtenerPacientes();
    }

    // ── Validaciones ──────────────────────────────────────────────
    public void validar(Paciente p, List<Paciente> actuales, boolean esNuevo) {
        if (p.getCurp().isBlank())
            throw new IllegalArgumentException("La CURP no puede estar vacía.");
        if (p.getCurp().length() < 18)
            throw new IllegalArgumentException("La CURP debe tener al menos 18 caracteres.");
        if (p.getNombre().isBlank() || p.getNombre().length() < 5)
            throw new IllegalArgumentException("El nombre debe tener al menos 5 caracteres.");
        if (p.getEdad() < 0 || p.getEdad() > 120)
            throw new IllegalArgumentException("La edad debe estar entre 0 y 120.");
        if (!p.getTelefono().matches("\\d{10}"))
            throw new IllegalArgumentException("El teléfono debe tener exactamente 10 dígitos.");
        if (p.getEstatus().isBlank())
            throw new IllegalArgumentException("El estatus no puede estar vacío.");

        if (esNuevo) {
            for (Paciente a : actuales) {
                if (a.getCurp().equalsIgnoreCase(p.getCurp()))
                    throw new IllegalArgumentException("Ya existe un paciente con esa CURP.");
>>>>>>> dev
            }
        }
    }

<<<<<<< HEAD
=======
    // ── Guardar lista completa en archivo ─────────────────────────
>>>>>>> dev
    public void guardarCambios(List<Paciente> lista) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Paciente p : lista) lineas.add(p.toString());
        repo.saveAllLines(lineas);
    }

<<<<<<< HEAD

=======
    // ── Alias para compatibilidad con MainController ──────────────
    public void saveAllPacientes(List<Paciente> lista) throws IOException {
        guardarCambios(lista);
    }

    // ── Alias para compatibilidad con ObservableList ──────────────
    public void saveAllPacientes(javafx.collections.ObservableList<Paciente> lista)
            throws IOException {
        guardarCambios(new ArrayList<>(lista));
    }

    // ── Alta de paciente ──────────────────────────────────────────
>>>>>>> dev
    public void agregarPaciente(Paciente nuevo) throws IOException {
        List<Paciente> actuales = obtenerPacientes();
        validar(nuevo, actuales, true);
        actuales.add(nuevo);
        guardarCambios(actuales);
    }

<<<<<<< HEAD
=======
    // ── Editar paciente existente ─────────────────────────────────
    public void editarPaciente(Paciente editado) throws IOException {
        List<Paciente> lista = obtenerPacientes();
        validar(editado, lista, false);
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCurp().equalsIgnoreCase(editado.getCurp())) {
                lista.set(i, editado);
                break;
            }
        }
        guardarCambios(lista);
    }

    // ── Eliminar paciente por CURP ────────────────────────────────
    public void eliminarPaciente(String curp) throws IOException {
        List<Paciente> lista = obtenerPacientes();
        lista.removeIf(p -> p.getCurp().equalsIgnoreCase(curp));
        guardarCambios(lista);
    }

    // ── Cambiar estatus ACTIVO <-> INACTIVO ───────────────────────
>>>>>>> dev
    public void cambiarEstatus(String curp) throws IOException {
        List<Paciente> lista = obtenerPacientes();
        for (Paciente p : lista) {
            if (p.getCurp().equalsIgnoreCase(curp)) {
<<<<<<< HEAD
                String nuevoEstatus = p.getEstatus().equalsIgnoreCase("activo") ? "inactivo" : "activo";
=======
                String nuevoEstatus = p.getEstatus().equalsIgnoreCase("ACTIVO")
                        ? "INACTIVO" : "ACTIVO";
>>>>>>> dev
                p.setEstatus(nuevoEstatus);
                break;
            }
        }
        guardarCambios(lista);
    }

<<<<<<< HEAD
    public long totalActivos() throws IOException {
        int count = 0;
        for (Paciente p : obtenerPacientes())
            if (p.getEstatus().equalsIgnoreCase("activo")) count++;
=======
    // ── Contadores ────────────────────────────────────────────────
    public long totalActivos() throws IOException {
        long count = 0;
        for (Paciente p : obtenerPacientes())
            if (p.getEstatus().equalsIgnoreCase("ACTIVO")) count++;
>>>>>>> dev
        return count;
    }

    public long totalInactivos() throws IOException {
<<<<<<< HEAD
        int count = 0;
        for (Paciente p : obtenerPacientes())
            if (p.getEstatus().equalsIgnoreCase("inactivo")) count++;
        return count;
    }
}
=======
        long count = 0;
        for (Paciente p : obtenerPacientes())
            if (p.getEstatus().equalsIgnoreCase("INACTIVO")) count++;
        return count;
    }

    public long totalPacientes() throws IOException {
        return obtenerPacientes().size();
    }
}
>>>>>>> dev
