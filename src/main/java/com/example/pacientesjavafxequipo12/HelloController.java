package com.example.pacientesjavafxequipo12;

import com.example.pacientesjavafxequipo12.models.Paciente;
import com.example.pacientesjavafxequipo12.services.PacienteService;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;

public class HelloController {
    @FXML private TableView<Paciente> tblPacientes;
    @FXML private TableColumn<Paciente, String> colCurp, colNombre, colEstatus;
    @FXML private TableColumn<Paciente, Integer> colEdad;
    @FXML private TextField txtCurp, txtName, txtEdad, txtTel, txtAlergias;
    @FXML private Label lblTotal, lblActivos, lblInactivos, lblMsg;

    private ObservableList<Paciente> data = FXCollections.observableArrayList();
    private PacienteService service = new PacienteService();

    @FXML
    public void initialize() {
        colCurp.setCellValueFactory(new PropertyValueFactory<>("curp"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));

        tblPacientes.setItems(data);
        cargarDatos();

        tblPacientes.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null) {
                txtCurp.setText(val.getCurp()); txtCurp.setDisable(true);
                txtName.setText(val.getNombre()); txtEdad.setText(String.valueOf(val.getEdad()));
                txtTel.setText(val.getTelefono()); txtAlergias.setText(val.getAlergias());
            }
        });
    }

    @FXML
    public void onAgregar() {
        try {
            Paciente p = new Paciente(txtCurp.getText(), txtName.getText(), Integer.parseInt(txtEdad.getText()),
                    txtTel.getText(), txtAlergias.getText(), "ACTIVO");
            service.validar(p, data, true);
            data.add(p);
            service.guardarCambios(data);
            mostrarMsg("Paciente registrado", "green");
            limpiar();
        } catch (Exception e) { mostrarMsg(e.getMessage(), "red"); }
    }

    @FXML
    public void onActualizar() {
        Paciente sel = tblPacientes.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            sel.setNombre(txtName.getText()); sel.setEdad(Integer.parseInt(txtEdad.getText()));
            sel.setTelefono(txtTel.getText()); sel.setAlergias(txtAlergias.getText());
            service.validar(sel, data, false);
            service.guardarCambios(data);
            tblPacientes.refresh();
            mostrarMsg("Actualizado", "blue");
        } catch (Exception e) { mostrarMsg(e.getMessage(), "red"); }
    }

    @FXML
    public void onEliminar() {
        Paciente sel = tblPacientes.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Inactivar paciente?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sel.setEstatus("INACTIVO");
                try { service.guardarCambios(data); tblPacientes.refresh(); cargarDatos(); } catch (IOException e) {}
            }
        });
    }

    private void cargarDatos() {
        try {
            data.setAll(service.obtenerPacientes());
            lblTotal.setText("Total: " + data.size());
            lblActivos.setText("Activos: " + data.stream().filter(p -> p.getEstatus().equals("ACTIVO")).count());
            lblInactivos.setText("Inactivos: " + data.stream().filter(p -> p.getEstatus().equals("INACTIVO")).count());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void mostrarMsg(String m, String color) {
        lblMsg.setText(m); lblMsg.setStyle("-fx-text-fill: " + color);
        cargarDatos();
    }

    private void limpiar() {
        txtCurp.clear(); txtCurp.setDisable(false); txtName.clear(); txtEdad.clear(); txtTel.clear(); txtAlergias.clear();
    }
}