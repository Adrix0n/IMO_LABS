module com.example.imolab1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires gs.core;


    opens com.example.imolab1 to javafx.fxml;
    exports com.example.imolab1;
}