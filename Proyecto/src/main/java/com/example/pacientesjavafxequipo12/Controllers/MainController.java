package com.example.pacientesjavafxequipo12.Controllers;

import com.example.pacientesjavafxequipo12.models.Paciente;
import com.example.pacientesjavafxequipo12.service.PacienteService;
import com.example.pacientesjavafxequipo12.utils.Paths;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class   MainController {

    //Tabla y columnas
    @FXML private TableView<Paciente>              tbl_productos;
    @FXML private TableColumn<Paciente, String>  col_id;
    @FXML private TableColumn<Paciente, String>  col_nombre;
    @FXML private TableColumn<Paciente, Integer> col_edad;
    @FXML private TableColumn<Paciente, String>  col_telefono;
    @FXML private TableColumn<Paciente, String>  col_alergias;
    @FXML private TableColumn<Paciente, String>  col_estatus;

    // Búsqueda y mensajes
    @FXML private TextField txt_buscar;
    @FXML private Label     lbl_error;

    //Predeterminados
    @FXML private Label lbl_total;
    @FXML private Label lbl_activos;
    @FXML private Label lbl_inactivos;

    //Estado interno
    // 1.  "Lista Inteligente":
    private final ObservableList<Paciente>listaPacientes = FXCollections.observableArrayList();

    // 2. "Motor de Lógica":
    private final PacienteService service = new PacienteService();


    @FXML
    public void initialize() {
        col_id.setCellValueFactory(new PropertyValueFactory<>("curp"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_edad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_alergias.setCellValueFactory(new PropertyValueFactory<>("alergias"));
        col_estatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));

        tbl_productos.setRowFactory(tv -> new TableRow<Paciente>() {
            @Override
            protected void updateItem(Paciente p, boolean vacio) {
                super.updateItem(p, vacio);

                if (p != null && "INACTIVO".equalsIgnoreCase(p.getEstatus())) {
                    setStyle("-fx-background-color: #ffcccc;"); // Rojo claro
                } else {
                    setStyle(""); // Estilo normal para el resto
                }
            }
        });

        // 3. FILTRADO (Búsqueda en tiempo real)
        FilteredList<Paciente> listaFiltrada = new FilteredList<>(listaPacientes, p -> true);

        //escucha al lbl buscar
        txt_buscar.textProperty().addListener((obs, oldVal, newVal) ->
                listaFiltrada.setPredicate(paciente -> {
                    //si no ha escrito nada muestra todos los pacientes
                    if (newVal == null || newVal.isEmpty()) return true;

                    String filtro = newVal.toLowerCase();

                    return paciente.getNombre().toLowerCase().contains(filtro)
                            || paciente.getCurp().toLowerCase().contains(filtro);
                })
        );

        // 4. ORDENAMIENTO
        SortedList<Paciente> listaOrdenada = new SortedList<>(listaFiltrada);

        listaOrdenada.comparatorProperty().bind(tbl_productos.comparatorProperty());

        // 5. ASIGNACIÓN FINAL
        tbl_productos.setItems(listaOrdenada);

        // Cargamos los datos desde el archivo CSV al iniciar.
        onReload();
    }

    //metodos privados
    private void actualizarResumen() {
        long activos = listaPacientes.stream()
                .filter(p -> p.getEstatus().equalsIgnoreCase("ACTIVO"))
                .count();

        long inactivos = listaPacientes.size() - activos;

        // Ponemos los resultados en los Labels de la interfaz (lbl_total, etc.)
        lbl_total.setText(String.valueOf(listaPacientes.size()));
        lbl_activos.setText(String.valueOf(activos));
        lbl_inactivos.setText(String.valueOf(inactivos));
    }

    private void mostrarError(String mensaje) {
        // Cambiamos la letra a color rojo
        lbl_error.setTextFill(javafx.scene.paint.Color.RED);
        // Ponemos el texto del error
        lbl_error.setText(mensaje);
    }

    @FXML
    public void onOpenAddForm() {
       //le decimos que queremos subir un paciente nuevo
        abrirFormulario(null);

    }

    //metodo auxiliar
    private void abrirFormulario(Paciente paciente) {

        lbl_error.setText("");

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.FORM_VIEW));
            Parent root = loader.load();

            FormController controller = loader.getController();

            controller.initData(paciente, listaPacientes, service);//comofunciona

            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root,800,600));
            stage.showAndWait();

            tbl_productos.refresh();
            actualizarResumen();

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    //Recargar desde archivo
    @FXML
    public void onReload() {
        try {
            lbl_error.setText("");

            // 1. CARGA DE DATOS:
            listaPacientes.setAll(service.obtenerPacientes());

            // 2. ACTUALIZACIÓN VISUAL:
            actualizarResumen();

        } catch (Exception e) {
            // 3.EXEPTION:
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void onCambiarEstatus() {
        Paciente seleccionado = tbl_productos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Selecciona un paciente para cambiar su estatus.");
            return;
        }


        String nuevoEstatus = seleccionado.getEstatus()
                .equalsIgnoreCase("ACTIVO") ? "INACTIVO" : "ACTIVO";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cambiar Estatus");
        confirm.setHeaderText("¿Cambiar estatus de " + seleccionado.getNombre()
                + " a " + nuevoEstatus + "?");

        //ESPERAR RESPUESTA
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            //APLICAR CAMBIO
            seleccionado.setEstatus(nuevoEstatus);

            try {
               //PERSISTENCIA EN CSV
                service.guardarCambios(new java.util.ArrayList<>(listaPacientes));

                // 8. ACTUALIZAR INTERFAZ:
                tbl_productos.refresh();
                actualizarResumen();


                mostrarError("Estatus actualizado a: " + nuevoEstatus);

            } catch (IOException e) {
                mostrarError("Error al guardar cambio de estatus: " + e.getMessage());
            }
        }
    }

    //Editar paciente seleccionado
    @FXML
    public void onEditProduct() {
        Paciente seleccionado = tbl_productos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormulario(seleccionado);
        } else {
            mostrarError("Selecciona un paciente de la tabla para editar.");
        }
    }

    @FXML
    public void onDeleteProduct() {
        Paciente seleccionado = tbl_productos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un paciente de la tabla para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Paciente");
        alert.setHeaderText("¿Eliminar a: " + seleccionado.getNombre() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");
        //ESPERARESPUESTA
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            listaPacientes.remove(seleccionado);
            try {

                service.guardarCambios(new java.util.ArrayList<>(listaPacientes));

                actualizarResumen();

                mostrarError("Paciente eliminado correctamente.");
            } catch (IOException e) {
                mostrarError("Error al eliminar del archivo: " + e.getMessage());
            }
        }
    }
}