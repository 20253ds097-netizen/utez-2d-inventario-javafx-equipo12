module com.example.pacientesjavafxequipo12 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pacientesjavafxequipo12 to javafx.fxml;
    exports com.example.pacientesjavafxequipo12;
    opens com.example.pacientesjavafxequipo12.Controllers to javafx.fxml;
   opens com.example.pacientesjavafxequipo12.models to javafx.base;

}