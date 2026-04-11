package com.example.pacientesjavafxequipo12;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/com/example/pacientesjavafxequipo12/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Gestión de Pacientes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}