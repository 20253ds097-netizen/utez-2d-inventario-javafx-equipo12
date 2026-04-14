package com.example.pacientesjavafxequipo.Controllers;

import com.example.pacientesjavafxequipo.models.Paciente;
import com.example.pacientesjavafxequipo.service.PacienteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    // ── Tabla y columnas ──────────────────────────────────────────
    @FXML private TableView<Paciente>            tbl_productos;
    @FXML private TableColumn<Paciente, String>  col_id;        // CURP
    @FXML private TableColumn<Paciente, String>  col_nombre;
    @FXML private TableColumn<Paciente, Integer> col_edad;
    @FXML private TableColumn<Paciente, String>  col_telefono;
    @FXML private TableColumn<Paciente, String>  col_alergias;
    @FXML private TableColumn<Paciente, String>  col_estatus;

    // ── Búsqueda y mensajes ───────────────────────────────────────
    @FXML private TextField txt_buscar;
    @FXML private Label     lbl_titulo;
    @FXML private Label     lbl_error;

    // ── Botones ───────────────────────────────────────────────────
    @FXML private Button btn_nuevo;
    @FXML private Button btn_recargar;
    @FXML private Button btn_editar;
    @FXML private Button btn_eliminar;
    @FXML private Button btn_cambiarEstatus;

    // ── Resumen ───────────────────────────────────────────────────
    @FXML private Label lbl_total;
    @FXML private Label lbl_activos;
    @FXML private Label lbl_inactivos;

    // ── Estado interno ────────────────────────────────────────────
    private final ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();
    private final PacienteService service = new PacienteService();

    // ─────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Vincular columnas con propiedades del modelo Paciente
        col_id.setCellValueFactory(new PropertyValueFactory<>("curp"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_edad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_alergias.setCellValueFactory(new PropertyValueFactory<>("alergias"));
        col_estatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));

        // Búsqueda en tiempo real por nombre o CURP
        FilteredList<Paciente> listaFiltrada = new FilteredList<>(listaPacientes, p -> true);

        txt_buscar.textProperty().addListener((obs, oldVal, newVal) -> {
            listaFiltrada.setPredicate(paciente -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filtro = newVal.toLowerCase();
                return paciente.getNombre().toLowerCase().contains(filtro)
                        || paciente.getCurp().toLowerCase().contains(filtro);
            });
        });

        SortedList<Paciente> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tbl_productos.comparatorProperty());
        tbl_productos.setItems(listaOrdenada);

        onReload();
    }

    // ── Abrir formulario para nuevo paciente ──────────────────────
    @FXML
    public void onOpenAddForm() {
        abrirFormulario(null);
    }

    // ── Abrir formulario para editar paciente seleccionado ────────
    @FXML
    public void onEditProduct() {
        Paciente seleccionado = tbl_productos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormulario(seleccionado);
        } else {
            lbl_error.setText("Selecciona un paciente de la tabla para editar.");
        }
    }

    // ── Cambiar estatus ACTIVO <-> INACTIVO (borrado lógico) ──────
    @FXML
    public void onCambiarEstatus() {
        Paciente seleccionado = tbl_productos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lbl_error.setText("Selecciona un paciente para cambiar su estatus.");
            return;
        }

        // Alternar estatus
        String nuevoEstatus = seleccionado.getEstatus().equalsIgnoreCase("ACTIVO")
                ? "INACTIVO"
                : "ACTIVO";
        seleccionado.setEstatus(nuevoEstatus);

        try {
            service.saveAllPacientes(listaPacientes);
            tbl_productos.refresh();
            actualizarResumen();
            lbl_error.setText("Estatus actualizado a: " + nuevoEstatus);
        } catch (IOException e) {
            lbl_error.setText("Error al guardar cambio de estatus: " + e.getMessage());
        }
    }

    // ── Recargar datos desde archivo ──────────────────────────────
    @FXML
    public void onReload() {
        try {
            lbl_error.setText("");
            listaPacientes.setAll(service.loadPacientes());
            actualizarResumen();
        } catch (IOException e) {
            lbl_error.setText("Error al cargar datos: " + e.getMessage());
        }
    }

    // ── Eliminar paciente con confirmación ────────────────────────
    @FXML
    public void onDeleteProduct() {
        Paciente seleccionado = tbl_productos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lbl_error.setText("Selecciona un paciente de la tabla para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Paciente");
        alert.setHeaderText("¿Eliminar a: " + seleccionado.getNombre() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            listaPacientes.remove(seleccionado);
            try {
                service.saveAllPacientes(listaPacientes);
                actualizarResumen();
                lbl_error.setText("Paciente eliminado correctamente.");
            } catch (IOException e) {
                lbl_error.setText("Error al eliminar del archivo: " + e.getMessage());
            }
        }
    }

    // ── Actualizar contadores del resumen ─────────────────────────
    private void actualizarResumen() {
        long activos   = listaPacientes.stream()
                .filter(p -> p.getEstatus().equalsIgnoreCase("ACTIVO"))
                .count();
        long inactivos = listaPacientes.size() - activos;

        lbl_total.setText(String.valueOf(listaPacientes.size()));
        lbl_activos.setText(String.valueOf(activos));
        lbl_inactivos.setText(String.valueOf(inactivos));
    }

    // ── Abrir ventana de formulario ───────────────────────────────
    private void abrirFormulario(Paciente paciente) {
        lbl_error.setText("");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/pacientesjavafxequipo12/views/form-view.fxml"));
            Parent root = loader.load();

            FormController controller = loader.getController();
            controller.initData(paciente, listaPacientes, service);

            Stage stage = new Stage();
            stage.setTitle(paciente == null ? "Agregar Nuevo Paciente" : "Editar Paciente");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            tbl_productos.refresh();
            actualizarResumen();

        } catch (IOException e) {
            lbl_error.setText("No se pudo abrir el formulario: " + e.getMessage());
        }
    }
}