package com.example.pacientesjavafxequipo.Controllers;

import com.example.pacientesjavafxequipo.models.Paciente;
import com.example.pacientesjavafxequipo.service.PacienteService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class FormController {

    @FXML private TextField txtCurp;
    @FXML private TextField txtNombre;
    @FXML private TextField txtEdad;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtAlergias;
    @FXML private Label     lblError;

    private final PacienteService service = new PacienteService();
    private MainController mainController;

    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    @FXML
    public void guardar() {
        try {
            Paciente nuevo = new Paciente(
                txtCurp.getText().trim(),
                txtNombre.getText().trim(),
                Integer.parseInt(txtEdad.getText().trim()),
                txtTelefono.getText().trim(),
                txtAlergias.getText().trim(),
                "activo"
            );
            service.agregarPaciente(nuevo);

            if (mainController != null) mainController.cargarPacientes();
            cerrarVentana();

        } catch (NumberFormatException e) {
            lblError.setText("La edad debe ser un número entero.");
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        } catch (IOException e) {
            lblError.setText("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) txtCurp.getScene().getWindow()).close();
    }
}