module org.example.spacesim2d {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.spacesim2d to javafx.fxml;
    exports org.example.spacesim2d;
}