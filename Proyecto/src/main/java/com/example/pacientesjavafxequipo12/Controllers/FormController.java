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
        // 1. Llenamos el ComboBox (menú desplegable) con las dos opciones disponibles.
        cmb_estatus.getItems().addAll("ACTIVO", "INACTIVO");

        // 2. Por defecto, dejamos seleccionada la opción "ACTIVO" para ahorrar tiempo al usuario.
        cmb_estatus.setValue("ACTIVO");
    }

    /**
     * Metodo para inyectar datos (Puente entre ventanas)
     * Este metodo NO es automatico. El MainController lo llama manualmente
     * justo despues de crear la ventana pero antes de mostrarla.
     */
    public void initData(Paciente paciente,
                         ObservableList<Paciente> lista,
                         PacienteService svc) {
        // 1. Guardamos las referencias en nuestras variables internas (this.) para usarlas luego.
        this.listaPacientes   = lista; // La lista que se ve en la tabla principal
        this.service          = svc;   // El servicio para validar y guardar
        this.pacienteEditando = paciente; // El paciente que vamos a editar (si aplica)

        // 2. Lógica de "Modo": ¿Es un paciente nuevo o uno existente?
        if (paciente != null) {
            //MODO EDICIÓN
            // Cambiamos el título de la ventana para que el usuario sepa que está editando.
            lbl_titulo_form.setText("Editar Paciente");

            // Llenamos los campos de texto con los datos actuales del paciente.
            txt_curp.setText(paciente.getCurp());

            // Bloqueamos el campo CURP. En bases de datos, el ID (CURP) no suele cambiarse.
            txt_curp.setDisable(true);

            txt_nombre.setText(paciente.getNombre());

            // Convertimos la edad (int) a texto (String) para que el TextField lo acepte.
            txt_edad.setText(String.valueOf(paciente.getEdad()));

            txt_telefono.setText(paciente.getTelefono());
            txt_alergias.setText(paciente.getAlergias());
            cmb_estatus.setValue(paciente.getEstatus());

        } else {
            // MODO ALTA (Nuevo Registro)
            // Si el objeto paciente llegó nulo, solo cambiamos el título a "Nuevo".
            // Los campos se quedan vacíos como están en el diseño original.
            lbl_titulo_form.setText("Nuevo Paciente");
        }
    }


    //Guardar (alta o edición)
    @FXML
    public void onGuardar() {

        lbl_error_form.setText("");

        // 1. Leer campos y los normaliza
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
            // EDICIÓN necesita hacer refresh despues
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
        // Simplemente llama a la función auxiliar para cerrar la pantalla actual
        cerrarVentana();
    }

//Helpers (Funciones de ayuda)

    // Este metodo sirve para mostrarle mensajes de advertencia al usuario en la interfaz
    private void mostrarError(String mensaje) {
        // Toma el texto que recibe por parámetro y lo pone en la etiqueta (Label) de error
        lbl_error_form.setText(mensaje);
    }

    // Este metodo contiene la lógica técnica para cerrar la ventana de forma segura
    private void cerrarVentana() {
        // 1. Obtenemos la "Escena" (Scene) donde está el botón cancelar.
        // 2. De esa escena, obtenemos la "Ventana" (Window) y la convertimos a un objeto tipo Stage.
        Stage stage = (Stage) btn_cancelar.getScene().getWindow();

        // 3. Le decimos a esa ventana que se cierre definitivamente.
        stage.close();
    }
}