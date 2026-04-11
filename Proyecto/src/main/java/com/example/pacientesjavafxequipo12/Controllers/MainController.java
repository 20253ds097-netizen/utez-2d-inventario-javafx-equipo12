package com.example.pacientesjavafxequipo12.Controllers;

import com.example.pacientesjavafxequipo12.models.Paciente;
import com.example.pacientesjavafxequipo12.service.PacienteService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, String>  colCurp;
    @FXML private TableColumn<Paciente, String>  colNombre;
    @FXML private TableColumn<Paciente, Integer> colEdad;
    @FXML private TableColumn<Paciente, String>  colTelefono;
    @FXML private TableColumn<Paciente, String>  colAlergias;
    @FXML private TableColumn<Paciente, String>  colEstatus;
    @FXML private Label lblResumen;

    private final PacienteService service = new PacienteService();

    @FXML
    public void initialize() {
        colCurp.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getCurp()));
        colNombre.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getNombre()));
        colEdad.setCellValueFactory(d ->
            new SimpleIntegerProperty(d.getValue().getEdad()).asObject());
        colTelefono.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getTelefono()));
        colAlergias.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getAlergias()));
        colEstatus.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getEstatus()));

        cargarPacientes();
    }

    @FXML
    public void cargarPacientes() {
        try {
            List<Paciente> lista = service.obtenerPacientes();
            tablaPacientes.setItems(FXCollections.observableArrayList(lista));
            lblResumen.setText("Activos: " + service.totalActivos() +
                               " | Inactivos: " + service.totalInactivos());
        } catch (IOException e) {
            mostrarError("Error al cargar pacientes: " + e.getMessage());
        }
    }

    @FXML
    public void cambiarEstatus() {
        Paciente seleccionado = tablaPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un paciente de la tabla primero.");
            return;
        }
        try {
            service.cambiarEstatus(seleccionado.getCurp());
            cargarPacientes();
        } catch (IOException e) {
            mostrarError("Error al cambiar estatus: " + e.getMessage());
        }
    }

    @FXML
    public void abrirFormulario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/pacientesjavafxequipo12/Form-view.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Paciente");
            stage.setScene(new Scene(loader.load()));

            FormController fc = loader.getController();
           // fc.setMainController(this);

            stage.showAndWait();
        } catch (IOException e) {
            mostrarError("Error al abrir formulario: " + e.getMessage());
        }
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}