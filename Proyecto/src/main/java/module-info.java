module com.example.pacientesjavafxequipo {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.pacientesjavafxequipo to javafx.fxml;
    opens com.example.pacientesjavafxequipo.Controllers to javafx.fxml;
    opens com.example.pacientesjavafxequipo.models to javafx.base;

    exports com.example.pacientesjavafxequipo;
    exports com.example.pacientesjavafxequipo.Controllers;
}