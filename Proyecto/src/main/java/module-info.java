module com.example.pacientesjavafxequipo12 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    opens com.example.pacientesjavafxequipo12 to javafx.fxml;
    opens com.example.pacientesjavafxequipo12.Controllers to javafx.fxml;
    opens com.example.pacientesjavafxequipo12.models to javafx.base;

    exports com.example.pacientesjavafxequipo12;
    exports com.example.pacientesjavafxequipo12.Controllers;
}