package com.example.pacientesjavafxequipo.service;

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
        }
        return lista;
    }

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
            }
        }
    }

    // ── Guardar lista completa en archivo ─────────────────────────
    public void guardarCambios(List<Paciente> lista) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Paciente p : lista) lineas.add(p.toString());
        repo.saveAllLines(lineas);
    }

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
    public void agregarPaciente(Paciente nuevo) throws IOException {
        List<Paciente> actuales = obtenerPacientes();
        validar(nuevo, actuales, true);
        actuales.add(nuevo);
        guardarCambios(actuales);
    }

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
    public void cambiarEstatus(String curp) throws IOException {
        List<Paciente> lista = obtenerPacientes();
        for (Paciente p : lista) {
            if (p.getCurp().equalsIgnoreCase(curp)) {
                String nuevoEstatus = p.getEstatus().equalsIgnoreCase("ACTIVO")
                        ? "INACTIVO" : "ACTIVO";
                p.setEstatus(nuevoEstatus);
                break;
            }
        }
        guardarCambios(lista);
    }

    // ── Contadores ────────────────────────────────────────────────
    public long totalActivos() throws IOException {
        long count = 0;
        for (Paciente p : obtenerPacientes())
            if (p.getEstatus().equalsIgnoreCase("ACTIVO")) count++;
        return count;
    }

    public long totalInactivos() throws IOException {
        long count = 0;
        for (Paciente p : obtenerPacientes())
            if (p.getEstatus().equalsIgnoreCase("INACTIVO")) count++;
        return count;
    }

    public long totalPacientes() throws IOException {
        return obtenerPacientes().size();
    }
}
