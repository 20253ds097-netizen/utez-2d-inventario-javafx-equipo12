package com.example.pacientesjavafxequipo.Controllers;

import com.example.pacientesjavafxequipo.models.Paciente;
import com.example.pacientesjavafxequipo.service.PacienteService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class FormController {

    // ── Campos del formulario ─────────────────────────────────────
    @FXML private Label            lbl_titulo_form;
    @FXML private TextField        txt_curp;
    @FXML private TextField        txt_nombre;
    @FXML private TextField        txt_edad;
    @FXML private TextField        txt_telefono;
    @FXML private TextField        txt_alergias;
    @FXML private ComboBox<String> cmb_estatus;
    @FXML private Label            lbl_error_form;
    @FXML private Button           btn_guardar;
    @FXML private Button           btn_cancelar;

    // ── Estado interno ────────────────────────────────────────────
    private Paciente               pacienteEditando;
    private ObservableList<Paciente> listaPacientes;
    private PacienteService        service;

    // ─────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        cmb_estatus.getItems().addAll("ACTIVO", "INACTIVO");
        cmb_estatus.setValue("ACTIVO");
    }

    /**
     * Llamado desde MainController antes de mostrar la ventana.
     * paciente == null  →  modo ALTA
     * paciente != null  →  modo EDICIÓN
     */
    public void initData(Paciente paciente,
                         ObservableList<Paciente> lista,
                         PacienteService svc) {
        this.listaPacientes   = lista;
        this.service          = svc;
        this.pacienteEditando = paciente;

        if (paciente != null) {
            lbl_titulo_form.setText("Editar Paciente");
            txt_curp.setText(paciente.getCurp());
            txt_curp.setDisable(true);         // CURP no se puede cambiar
            txt_nombre.setText(paciente.getNombre());
            txt_edad.setText(String.valueOf(paciente.getEdad()));
            txt_telefono.setText(paciente.getTelefono());
            txt_alergias.setText(paciente.getAlergias());
            cmb_estatus.setValue(paciente.getEstatus());
        } else {
            lbl_titulo_form.setText("Nuevo Paciente");
        }
    }

    // ── Guardar (alta o edición) ──────────────────────────────────
    @FXML
    public void onGuardar() {
        lbl_error_form.setText("");

        // 1. Leer campos
        String curp     = txt_curp.getText().trim().toUpperCase();
        String nombre   = txt_nombre.getText().trim();
        String edadTxt  = txt_edad.getText().trim();
        String telefono = txt_telefono.getText().trim();
        String alergias = txt_alergias.getText().trim();
        String estatus  = cmb_estatus.getValue();

        // 2. Validaciones
        if (curp.isEmpty() || nombre.isEmpty() || edadTxt.isEmpty()
                || telefono.isEmpty() || estatus == null) {
            mostrarError("Todos los campos marcados con * son obligatorios.");
            return;
        }
        if (nombre.length() < 5) {
            mostrarError("El nombre debe tener al menos 5 caracteres.");
            return;
        }
        if (curp.length() < 18) {
            mostrarError("La CURP debe tener al menos 18 caracteres.");
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadTxt);
            if (edad < 0 || edad > 120) {
                mostrarError("La edad debe estar entre 0 y 120.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("La edad debe ser un número entero.");
            return;
        }

        if (!telefono.matches("\\d{10,}")) {
            mostrarError("El teléfono debe contener al menos 10 dígitos numéricos.");
            return;
        }

        // 3. Verificar CURP duplicada (solo en alta)
        if (pacienteEditando == null) {
            boolean existe = listaPacientes.stream()
                    .anyMatch(p -> p.getCurp().equalsIgnoreCase(curp));
            if (existe) {
                mostrarError("Ya existe un paciente con esa CURP.");
                return;
            }
        }

        // 4. Crear o actualizar objeto
        if (pacienteEditando == null) {
            listaPacientes.add(new Paciente(curp, nombre, edad,
                                            telefono, alergias, estatus));
        } else {
            pacienteEditando.setNombre(nombre);
            pacienteEditando.setEdad(edad);
            pacienteEditando.setTelefono(telefono);
            pacienteEditando.setAlergias(alergias);
            pacienteEditando.setEstatus(estatus);
        }

        // 5. Persistir en archivo
        try {
            service.guardarCambios(new java.util.ArrayList<>(listaPacientes));
            cerrarVentana();
        } catch (IOException e) {
            mostrarError("Error al guardar en archivo: " + e.getMessage());
        }
    }

    // ── Cancelar sin guardar ──────────────────────────────────────
    @FXML
    public void onCancelar() {
        cerrarVentana();
    }

    // ── Helpers ───────────────────────────────────────────────────
    private void mostrarError(String mensaje) {
        lbl_error_form.setText(mensaje);
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btn_cancelar.getScene().getWindow();
        stage.close();
    }
}