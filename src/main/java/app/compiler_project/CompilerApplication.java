package app.compiler_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CompilerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CompilerApplication.class.getResource("compiler_code_form.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1940, 1000);
        stage.setTitle("Compiler");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}