module app.compiler_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.compiler_project to javafx.fxml;
    exports app.compiler_project;
}