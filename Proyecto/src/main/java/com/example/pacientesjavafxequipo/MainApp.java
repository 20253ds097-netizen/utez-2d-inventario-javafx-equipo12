package com.example.pacientesjavafxequipo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pacientesjavafxequipo.views/Main-view.fxml"));
     Scene scene = new Scene(loader.load());
        stage.setTitle("Gestión_de_Pacientes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}