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
    @FXML private TableView<Paciente>            tbl_productos;
    @FXML private TableColumn<Paciente, String>  col_id;
    @FXML private TableColumn<Paciente, String>  col_nombre;
    @FXML private TableColumn<Paciente, Integer> col_edad;
    @FXML private TableColumn<Paciente, String>  col_telefono;
    @FXML private TableColumn<Paciente, String>  col_alergias;
    @FXML private TableColumn<Paciente, String>  col_estatus;

    // Búsqueda y mensajes
    @FXML private TextField txt_buscar;
    @FXML private Label     lbl_titulo;
    @FXML private Label     lbl_error;

    //Resumen
    @FXML private Label lbl_total;
    @FXML private Label lbl_activos;
    @FXML private Label lbl_inactivos;

    //Estado interno
    // 1.  "Lista Inteligente":
// la ObservableList le avisa automáticamente a la Tabla (TableView)
// cada vez que se agrega, borra o modifica un paciente para que se pinte en pantalla.
    private final ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

    // 2. "Motor de Lógica":
// Aquí creamos una instancia del Servicio. El controlador no sabe cómo guardar
// archivos ni cómo validar si un nombre es muy corto; por eso tiene a su "asistente"
// (el service) para delegarle esas tareas pesadas.
    private final PacienteService service = new PacienteService();


    @FXML
    public void initialize() {
        // 1. VINCULACIÓN DE COLUMNAS (Mapeo)
        // El PropertyValueFactory busca los "Getters" en la clase Paciente.
        // Ejemplo: "curp" busca el metodo getCurp() para poner el valor en esa columna.
        col_id.setCellValueFactory(new PropertyValueFactory<>("curp"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_edad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_alergias.setCellValueFactory(new PropertyValueFactory<>("alergias"));
        col_estatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));

        // 2. PERSONALIZACIÓN DE FILAS (Diseño condicional)
        // Pintar de rojo las filas de pacientes INACTIVOS
        tbl_productos.setRowFactory(tv -> new TableRow<Paciente>() {
            @Override
            protected void updateItem(Paciente p, boolean vacio) {
                super.updateItem(p, vacio);

                // Si la fila tiene un paciente y su estatus es INACTIVO
                if (p != null && "INACTIVO".equalsIgnoreCase(p.getEstatus())) {
                    setStyle("-fx-background-color: #ffcccc;"); // Rojo claro
                } else {
                    setStyle(""); // Estilo normal para el resto
                }
            }
        });

        // 3. FILTRADO (Búsqueda en tiempo real)
        // Creamos una "Lista Filtrada" que envuelve a nuestra lista original.
        FilteredList<Paciente> listaFiltrada = new FilteredList<>(listaPacientes, p -> true);

        // Escuchamos cada vez que el usuario escribe una letra en el cuadro de búsqueda.
        txt_buscar.textProperty().addListener((obs, oldVal, newVal) ->
                listaFiltrada.setPredicate(paciente -> {
                    // Si el buscador está vacío, mostramos a todos.
                    if (newVal == null || newVal.isEmpty()) return true;

                    String filtro = newVal.toLowerCase();
                    // Si el nombre o la CURP contienen lo que el usuario escribió, se queda en la lista.
                    return paciente.getNombre().toLowerCase().contains(filtro)
                            || paciente.getCurp().toLowerCase().contains(filtro);
                })
        );

        // 4. ORDENAMIENTO (Sorting)
        // Envolvemos la lista filtrada en una "Lista Ordenable".
        SortedList<Paciente> listaOrdenada = new SortedList<>(listaFiltrada);
        // Sincronizamos el orden de la lista con los clics que el usuario haga en las cabeceras de la tabla.
        listaOrdenada.comparatorProperty().bind(tbl_productos.comparatorProperty());

        // 5. ASIGNACIÓN FINAL
        // Le pasamos a la tabla la lista que ya puede filtrar y ordenar.
        tbl_productos.setItems(listaOrdenada);

        // Cargamos los datos desde el archivo CSV al iniciar.
        onReload();
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

    // cambiar estatus ACTIVO <-> INACTIVO
    @FXML
    public void onCambiarEstatus() {
        // 1. OBTENER SELECCIÓN: Miramos qué fila tiene seleccionada el usuario en la tabla.
        Paciente seleccionado = tbl_productos.getSelectionModel().getSelectedItem();

        // 2. VALIDAR SELECCIÓN: Si no hay nada seleccionado, avisamos y salimos del metodo.
        if (seleccionado == null) {
            mostrarError("Selecciona un paciente para cambiar su estatus.");
            return;
        }

        // 3. LÓGICA DE INTERRUPTOR (Toggle):
        // Si el estatus es ACTIVO, el nuevo será INACTIVO, y viceversa.
        String nuevoEstatus = seleccionado.getEstatus()
                .equalsIgnoreCase("ACTIVO") ? "INACTIVO" : "ACTIVO";

        // 4. CONFIRMACIÓN: Creamos una ventanita de alerta para que el usuario confirme.
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cambiar Estatus");
        confirm.setHeaderText("¿Cambiar estatus de " + seleccionado.getNombre()
                + " a " + nuevoEstatus + "?");

        // 5. ESPERAR RESPUESTA: El programa se detiene hasta que el usuario pique "OK" o "Cancelar".
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            // 6. APLICAR CAMBIO: Si aceptó, actualizamos el atributo en el objeto.
            seleccionado.setEstatus(nuevoEstatus);

            try {
                // 7. PERSISTENCIA: Guardamos la lista completa actualizada en el archivo CSV.
                service.guardarCambios(new java.util.ArrayList<>(listaPacientes));

                // 8. ACTUALIZAR INTERFAZ:
                tbl_productos.refresh(); // Fuerza a la tabla a redibujarse (y aplicar los colores de fila)
                actualizarResumen();     // Recalcula el total de activos/inactivos

                // 9. FEEDBACK POSITIVO: Pintamos el mensaje de éxito en verde.
                lbl_error.setTextFill(javafx.scene.paint.Color.GREEN);
                lbl_error.setText("Estatus actualizado a: " + nuevoEstatus);

            } catch (IOException e) {
                // Si algo falla al escribir el archivo, mostramos el error.
                mostrarError("Error al guardar cambio de estatus: " + e.getMessage());
            }
        }
    }

    //Recargar desde archivo
    @FXML
    public void onReload() {
        try {
            // 1. LIMPIEZA: Quitamos cualquier mensaje de error que estuviera escrito en la pantalla.
            lbl_error.setText("");

            // 2. CARGA DE DATOS:
            // service.obtenerPacientes() va al archivo CSV, lee cada línea y las convierte en objetos.
            // listaPacientes.setAll(...) toma esos objetos y REEMPLAZA todo lo que habia en la
            // tabla actualmente. Así nos aseguramos de tener la versión más reciente del archivo.
            listaPacientes.setAll(service.obtenerPacientes());

            // 3. ACTUALIZACIÓN VISUAL:
            // Como los datos cambiaron, llamamos a esta función para que vuelva a contar
            // cuántos pacientes hay en total, cuántos activos y cuántos inactivos.
            actualizarResumen();

        } catch (Exception e) {
            // 4. MANEJO DE ERRORES:
            // Si el archivo está corrupto, no existe o no hay permisos, el programa no se cierra.
            // En su lugar, atrapa el error y lo muestra en letras rojas al usuario.
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void onDeleteProduct() {
        // 1. ¿Hay alguien seleccionado?
        Paciente seleccionado = tbl_productos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un paciente de la tabla para eliminar.");
            return;
        }

        // 2. PREGUNTAR: Creamos una alerta de confirmación (Botones Aceptar/Cancelar).
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Paciente");
        alert.setHeaderText("¿Eliminar a: " + seleccionado.getNombre() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");

        // 3. SEGUIMIENTO: Si el usuario presiona OK
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Borramos de la lista visual (la que ve el usuario)
            listaPacientes.remove(seleccionado);
            try {
                // Guardamos la lista (ya sin ese paciente) en el archivo CSV
                service.guardarCambios(new java.util.ArrayList<>(listaPacientes));

                actualizarResumen(); // Recalculamos totales
                lbl_error.setTextFill(javafx.scene.paint.Color.GREEN);
                lbl_error.setText("Paciente eliminado correctamente.");
            } catch (IOException e) {
                mostrarError("Error al eliminar del archivo: " + e.getMessage());
            }
        }
    }

    //Helpers privados
    private void actualizarResumen() {
        // .stream() recorre la lista uno por uno.
        // .filter() solo deja pasar a los que son "ACTIVO".
        // .count() cuenta cuántos pasaron el filtro.
        long activos = listaPacientes.stream()
                .filter(p -> p.getEstatus().equalsIgnoreCase("ACTIVO"))
                .count();

        // Los inactivos son simplemente el Resto (Total menos Activos)
        long inactivos = listaPacientes.size() - activos;

        // Ponemos los resultados en los Labels de la interfaz (lbl_total, etc.)
        lbl_total.setText(String.valueOf(listaPacientes.size()));
        lbl_activos.setText(String.valueOf(activos));
        lbl_inactivos.setText(String.valueOf(inactivos));
    }

    private void mostrarError(String mensaje) {
        // Cambiamos el color de la letra a rojo
        lbl_error.setTextFill(javafx.scene.paint.Color.RED);
        // Ponemos el texto del error
        lbl_error.setText(mensaje);
    }

    //Abrir formulario para un Nuevo Paciente
    @FXML
    public void onOpenAddForm() {
        abrirFormulario(null);
    }
   //metodo auxiliar para no abrir ventana dos veces
    private void abrirFormulario(Paciente paciente) {
        // 1. Limpiamos cualquier mensaje de error previo en la pantalla principal.
        lbl_error.setText("");

        try {
            // 2. Cargamos el archivo FXML (el diseño visual de la ventanita).
            // FXMLLoader es el "traductor" que convierte el XML en objetos de Java.
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.FORM_VIEW));
            Parent root = loader.load();

            // 3. instacia
            FormController controller = loader.getController();

            // 4. Le pasamos los datos necesarios: el paciente (si es editar), la lista y el servicio.
            // Esto conecta ambas pantallas.
            controller.initData(paciente, listaPacientes, service);

            // 5. Creamos un nuevo "Escenario" (Stage) para la ventana emergente.
            Stage stage = new Stage();

            // 6. BLOQUEO (Modality): Esta línea hace que no puedas picar la ventana de atrás
            // hasta que cierres la de adelante. Evita que el usuario haga desastres.
            stage.initModality(Modality.APPLICATION_MODAL);

            // 7. Ponemos el diseño (root) dentro del escenario y prohibimos cambiar el tamaño.
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            // 8. PAUSA: El programa se detiene aquí con "showAndWait()".
            // La ventana se queda abierta y el código no sigue hasta que la cierres.
            stage.showAndWait();

            // 9. REFRESCAR: Una vez que se cierra la ventana, actualizamos la tabla
            // y los numeritos del resumen (Total, Activos, etc.) por si hubo cambios.
            tbl_productos.refresh();
            actualizarResumen();

        } catch (IOException e) {
            // Si el archivo FXML no existe o tiene un error, mostramos el mensaje.
            mostrarError("No se pudo abrir el formulario: " + e.getMessage());
        }
    }
}