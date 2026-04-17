package com.example.pacientesjavafxequipo12.Controllers;

import com.example.pacientesjavafxequipo12.models.Paciente;
import com.example.pacientesjavafxequipo12.service.PacienteService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

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
    private Paciente                 pacienteEditando;
    private ObservableList<Paciente> listaPacientes;
    private PacienteService          service;

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
            txt_curp.setDisable(true);   // CURP no se puede cambiar
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

        // 2. validad la  edad antes de crear el objeto
        int edad;
        try {
            edad = Integer.parseInt(edadTxt);
        } catch (NumberFormatException e) {
            mostrarError("La edad debe ser un número entero.");
            return;
        }

        // 3. Construir objeto temporal para validar
        Paciente temporal = new Paciente(curp, nombre, edad,
                                         telefono, alergias,
                                         estatus != null ? estatus : "");

        // 4. Validar usando el service (lanza IllegalArgumentException si algo falla)
        try {
            service.validar(temporal, new ArrayList<>(listaPacientes),
                            pacienteEditando == null);
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return;
        }

        // 5. Crear o actualizar el objeto en la lista
        if (pacienteEditando == null) {
            // ALTA
            listaPacientes.add(temporal);
        } else {
            // EDICIÓN
            pacienteEditando.setNombre(nombre);
            pacienteEditando.setEdad(edad);
            pacienteEditando.setTelefono(telefono);
            pacienteEditando.setAlergias(alergias);
            pacienteEditando.setEstatus(estatus);
        }

        // 6. Persistir en archivo
        try {
            service.guardarCambios(new ArrayList<>(listaPacientes));
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