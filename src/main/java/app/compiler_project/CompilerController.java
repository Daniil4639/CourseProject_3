package app.compiler_project;

import app.compiler_project.assembler_part.AssemblerApplication;
import app.compiler_project.assembler_part.AssemblerController;
import app.compiler_project.assembler_part.AssemblerTransformator;
import app.compiler_project.lexical_part.LexicalAnalyzer;
import app.compiler_project.lexical_part.LexicalApplication;
import app.compiler_project.lexical_part.LexicalController;
import app.compiler_project.lexical_part.ResultsLexicalPackage;
import app.compiler_project.poliz_part.PolizApplication;
import app.compiler_project.poliz_part.PolizConstructor;
import app.compiler_project.poliz_part.PolizController;
import app.compiler_project.semantic_part.SemanticAnalyzer;
import app.compiler_project.syntactic_part.SyntacticAnalyzer;
import app.compiler_project.syntactic_part.SyntacticApplication;
import app.compiler_project.syntactic_part.SyntacticController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class CompilerController {

    private final Stage lexicalStage = new Stage();
    private final Stage syntacticStage = new Stage();
    private final Stage polizStage = new Stage();
    private final Stage assemblerStage = new Stage();

    private LexicalApplication lexicalApplication;
    private LexicalController lexicalController;

    private ResultsLexicalPackage resultsLexicalPackage;
    private LexicalAnalyzer lexicalAnalyzer;

    private SyntacticApplication syntacticApplication;
    private SyntacticController syntacticController;

    private SyntacticAnalyzer syntacticAnalyzer;

    private PolizApplication polizApplication;
    private PolizController polizController;

    private List<String> poliz;
    private PolizConstructor polizConstructor;

    private AssemblerApplication assemblerApplication;
    private AssemblerController assemblerController;
    private AssemblerTransformator assemblerTransformator;

    @FXML
    private TextArea codeSegment;

    @FXML
    void initialize() {
        initializeLexicalSegment();
        initializeSyntacticSegment();
        initializePolizSegment();
        initializeAssemblerSegment();

        resultsLexicalPackage = new ResultsLexicalPackage(new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        lexicalAnalyzer = new LexicalAnalyzer(resultsLexicalPackage);
        syntacticAnalyzer = new SyntacticAnalyzer();

        poliz = new ArrayList<>();
        polizConstructor = new PolizConstructor(poliz);

        assemblerTransformator = new AssemblerTransformator(resultsLexicalPackage, poliz);
    }

    @FXML
    void compileClick() {
        try {
            lexicalAnalyzer.analyze(codeSegment.getText());

            lexicalController.addParams(resultsLexicalPackage);

            List<String> syntacticResult = syntacticAnalyzer
                    .analyze(resultsLexicalPackage.resultArea());

            syntacticController.setResult(syntacticResult);

            SemanticAnalyzer.analyze(resultsLexicalPackage.resultArea(),
                    resultsLexicalPackage
                            .identifiersTable()
                            .stream()
                            .map(Pair::getValue)
                            .toList());

            polizConstructor.construct(resultsLexicalPackage.resultArea());
            polizController.setResult(poliz);

            assemblerTransformator.transformToAssembler();
            assemblerController.setResult(assemblerTransformator.getString());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Compilation had successfully done!");
            alert.show();

        } catch (IllegalArgumentException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Analyzer error");
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

    @FXML
    void showSyntacticClicked() throws Exception {
        syntacticApplication.start(syntacticStage);
    }

    @FXML
    void showPolizClicked() throws Exception {
        polizApplication.start(polizStage);
    }

    @FXML
    void showAssemblerClicked() throws Exception {
        assemblerApplication.start(assemblerStage);
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

    private void initializeSyntacticSegment() {
        try {
            syntacticApplication = new SyntacticApplication();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CompilerApplication.class.getResource("syntactic_analyzer.fxml"));
            Scene scene = new Scene(loader.load(), 1189, 445);
            syntacticStage.setTitle("Syntactic analyzer");
            syntacticStage.setScene(scene);

            syntacticController = loader.getController();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void initializePolizSegment() {
        try {
            polizApplication = new PolizApplication();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CompilerApplication.class.getResource("poliz_constructor.fxml"));
            Scene scene = new Scene(loader.load(), 1189, 445);
            polizStage.setTitle("Poliz constructor");
            polizStage.setScene(scene);

            polizController = loader.getController();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void initializeAssemblerSegment() {
        try {
            assemblerApplication = new AssemblerApplication();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CompilerApplication.class.getResource("assembler_transform.fxml"));
            Scene scene = new Scene(loader.load(), 600, 1050);
            assemblerStage.setTitle("Assembler View");
            assemblerStage.setScene(scene);

            assemblerController = loader.getController();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}