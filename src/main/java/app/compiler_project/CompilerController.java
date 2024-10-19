package app.compiler_project;

import app.compiler_project.lexical_part.LexicalAnalyzer;
import app.compiler_project.lexical_part.LexicalApplication;
import app.compiler_project.lexical_part.LexicalController;
import app.compiler_project.lexical_part.ResultsLexicalPackage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CompilerController {

    private final Stage lexicalStage = new Stage();

    private LexicalApplication lexicalApplication;
    private LexicalController lexicalController;

    private ResultsLexicalPackage resultsLexicalPackage;
    private LexicalAnalyzer lexicalAnalyzer;

    @FXML
    private TextArea codeSegment;

    @FXML
    void initialize() {
        initializeLexicalSegment();

        resultsLexicalPackage = new ResultsLexicalPackage(new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        lexicalAnalyzer = new LexicalAnalyzer(resultsLexicalPackage);
    }

    @FXML
    void compileClick() {
        try {
            lexicalAnalyzer.analyze(codeSegment.getText());

            lexicalController.addParams(resultsLexicalPackage);
        } catch (IllegalArgumentException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unknown token");
            alert.setContentText(ex.getMessage());
            alert.show();
        }
    }

    @FXML
    void buttonPressed(MouseEvent event) {
        Button button = (Button) event.getSource();
        String buttonPressedStyle = "-fx-background-color: gray; -fx-background-radius: 7px;" +
                "-fx-border-color: black; -fx-border-radius: 5px;";
        button.setStyle(buttonPressedStyle);
    }

    @FXML
    void buttonReleased(MouseEvent event) {
        Button button = (Button) event.getSource();
        String buttonReleasedStyle = "-fx-background-color: #fc625d; -fx-background-radius: 7px;" +
                "-fx-border-color: black; -fx-border-radius: 5px;";
        button.setStyle(buttonReleasedStyle);
    }

    @FXML
    void showLexicalClicked() throws Exception {
        lexicalApplication.start(lexicalStage);
    }

    private void initializeLexicalSegment() {
        try {
            lexicalApplication = new LexicalApplication();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CompilerApplication.class.getResource("lexical_analyzer.fxml"));
            Scene scene = new Scene(loader.load(), 1189, 916);
            lexicalStage.setTitle("Lexical analyzer");
            lexicalStage.setScene(scene);

            lexicalController = loader.getController();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}