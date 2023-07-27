module com.playersgamerecordsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.playersgamerecordsystem to javafx.fxml;
    exports com.playersgamerecordsystem;
}