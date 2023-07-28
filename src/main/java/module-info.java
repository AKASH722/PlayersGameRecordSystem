module com.playersgamerecordsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens exercise1 to javafx.fxml;
    exports exercise1;
}