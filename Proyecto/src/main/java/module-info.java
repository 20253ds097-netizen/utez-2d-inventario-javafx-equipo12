module com.example.pacientesjavafxequipo12 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pacientesjavafxequipo12 to javafx.fxml;
    exports com.example.pacientesjavafxequipo12;
}