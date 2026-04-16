package com.example.pacientesjavafxequipo12.service;

import com.example.pacientesjavafxequipo12.models.Paciente;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteService {
    private final String NOMBRE_ARCHIVO = "data/pacientes.csv";

    // CARGAR DATOS (Requisito 4.B)
    public List<Paciente> obtenerPacientes() throws IOException {
        List<Paciente> lista = new ArrayList<>();
        File file = new File(NOMBRE_ARCHIVO);

        if (!file.exists()) file.createNewFile();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] d = linea.split(",");
                if (d.length == 6) {
                    lista.add(new Paciente(d[0], d[1], Integer.parseInt(d[2]), d[3], d[4], d[5]));
                }
            }
        }
        return lista;
    }

    // GUARDAR DATOS (Requisito 4.B)
    public void guardarCambios(List<Paciente> lista) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO))) {
            for (Paciente p : lista) {
                bw.write(p.toString());
                bw.newLine();
            }
        }
    }

    // VALIDACIONES MÍNIMAS (Requisito 4.C)
    public void validar(Paciente p, List<Paciente> actual, boolean esNuevo) throws IllegalArgumentException {
        // 1. No permitir campos vacíos
        if (p.getCurp().isEmpty() || p.getNombre().isEmpty() || p.getTelefono().isEmpty()) {
            throw new IllegalArgumentException("Error: Los campos con * no pueden estar vacíos.");
        }
        // 2. Nombre mínimo 5 caracteres
        if (p.getNombre().length() < 5) {
            throw new IllegalArgumentException("Error: El nombre debe tener al menos 5 caracteres.");
        }
        // 3. Edad en rango 0 a 120
        if (p.getEdad() < 0 || p.getEdad() > 120) {
            throw new IllegalArgumentException("Error: La edad debe ser entre 0 y 120.");
        }
        // 4. Teléfono solo dígitos y longitud mínima 10
        if (!p.getTelefono().matches("\\d{10,}")) {
            throw new IllegalArgumentException("Error: El teléfono debe tener al menos 10 dígitos numéricos.");
        }
        // 5. Evitar duplicados por CURP (Solo si es registro nuevo)
        if (esNuevo) {
            for (Paciente existente : actual) {
                if (existente.getCurp().equalsIgnoreCase(p.getCurp())) {
                    throw new IllegalArgumentException("Error: Ya existe un paciente con esta CURP.");
                }
            }
        }
    }
}