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

    //Campos del formulario
    @FXML private Label            lbl_titulo_form;
    @FXML private TextField        txt_curp;
    @FXML private TextField        txt_nombre;
    @FXML private TextField        txt_edad;
    @FXML private TextField        txt_telefono;
    @FXML private TextField        txt_alergias;
    @FXML private ComboBox<String> cmb_estatus;
    @FXML private Label            lbl_error_form;
    @FXML private Button           btn_cancelar;

    //Estado interno
    private Paciente                 pacienteEditando;
    private ObservableList<Paciente> listaPacientes;
    private PacienteService          service;

    //Metodo de inicializacion automatica
    @FXML
    public void initialize() {
        // 1. Opciones disponibles
        cmb_estatus.getItems().addAll("ACTIVO", "INACTIVO");
        // 2. Por defecto
        cmb_estatus.setValue("ACTIVO");
    }

    /**
     * Metodo para inyectar datos (Puente entre ventanas)
     * Este metodo NO es automatico. El MainController lo llama manualmente
     * justo despues de crear la ventana pero antes de mostrarla.
     */
    public void initData(Paciente paciente,
                         ObservableList<Paciente> lista,
                         PacienteService validar) {
         //variables globales para usar mas tarde
        this.listaPacientes   = lista;
        this.service          = validar;
        this.pacienteEditando = paciente;

        if (paciente != null) {

            lbl_titulo_form.setText("Editar Paciente");

            // Llenamos los campos con lo datos del paciente.
            txt_curp.setText(paciente.getCurp());
            txt_curp.setDisable(true);

            txt_nombre.setText(paciente.getNombre());

            // Convertimos la edad
            txt_edad.setText(String.valueOf(paciente.getEdad()));

            txt_telefono.setText(paciente.getTelefono());
            txt_alergias.setText(paciente.getAlergias());
            cmb_estatus.setValue(paciente.getEstatus());

        } else {
            lbl_titulo_form.setText("Nuevo Paciente");
        }
    }



    @FXML
    public void onGuardar() {

        lbl_error_form.setText("");

        // 1. Leer campos y los normaliza
        //trim() para quitar espacios accidentales al inicio o final
        // .toUpperCase() para que la CURP siempre esté en mayúsculas
        String curp     = txt_curp.getText().trim().toUpperCase();
        String nombre   = txt_nombre.getText().trim();
        String edadTxt  = txt_edad.getText().trim();
        String telefono = txt_telefono.getText().trim();
        String alergias = txt_alergias.getText().trim();
        String estatus  = cmb_estatus.getValue();

        // 2. validad la edad antes de crear el objeto
        //valida que la edad sea un numero entero
        int edad;
        try {
            edad = Integer.parseInt(edadTxt);
        } catch (NumberFormatException e) {
            mostrarError("La edad debe ser un número entero.");
            return;
        }


        // 3. Construir objeto temporal para validar si la validacion falla se descarta
        Paciente temporal = new Paciente(curp, nombre, edad,
                                         telefono, alergias,
                                         estatus != null ? estatus : "");

        // 4. Validar usando el service punto validad para obtener las
        // VALIDACIONES MÍNIMAS (Requisito 4.C)
        try {
            service.validar(temporal, new ArrayList<>(listaPacientes),
                            pacienteEditando == null);
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return;
        }

        // 5. Crear  el objeto en la lista sin refrescar
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

    //Cancelar sin guardar
// Este metodo se activa cuando el usuario hace clic en el boton "Cancelar"
    @FXML
    public void onCancelar() {
        cerrarVentana();
    }

//Helpers (Funciones de ayuda)

    private void mostrarError(String mensaje) {
        lbl_error_form.setText(mensaje);
    }

    // Este metodo contiene la lógica técnica para cerrar la ventana de forma segura
    private void cerrarVentana() {
        Stage stage = (Stage) btn_cancelar.getScene().getWindow();

        stage.close();
    }
}