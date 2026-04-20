package com.example.pacientesjavafxequipo12.service;

import com.example.pacientesjavafxequipo12.models.Paciente;
import com.example.pacientesjavafxequipo12.utils.Paths;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

        public class PacienteService {


            // CARGAR DATOS (Requisito 4.B)
            // METODO PARA LEER EL ARCHIVO
            public List<Paciente> obtenerPacientes() {

                List<Paciente> lista = new ArrayList<>();

                try {
                    //
                    File archivo = new File(Paths.NOMBRE_ARCHIVO);

                    //
                    if (archivo.getParentFile() != null) {
                        archivo.getParentFile().mkdirs();
                    }

                    //
                    if (!archivo.exists()) {
                        archivo.createNewFile();
                    }

                    BufferedReader br = new BufferedReader(new FileReader(archivo));
                    String linea;

                    while ((linea = br.readLine()) != null) {
                        String[] datos = linea.split(",");

                        if (datos.length == 6) {
                            Paciente p = new Paciente(
                                    datos[0],
                                    datos[1],
                                    Integer.parseInt(datos[2]),
                                    datos[3],
                                    datos[4],
                                    datos[5]
                            );
                            lista.add(p);
                        }
                    }

                    br.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return lista;
            }

            // GUARDAR DATOS (Requisito 4.B)
            public void guardarCambios(List<Paciente> lista) throws IOException {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.NOMBRE_ARCHIVO))) {
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
        // \\d se pone pa ra analizar que tenga la validacion de 10 dijitos

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