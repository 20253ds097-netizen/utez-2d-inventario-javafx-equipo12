package com.example.pacientesjavafxequipo12;

import com.example.pacientesjavafxequipo12.utils.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.GESTIONAR_ESTUDIANTES_VIEW));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Gestión_de_Pacientes");
        stage.setScene(scene);
        stage.show();
    }

}