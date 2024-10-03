package app.compiler_project;

import app.compiler_project.utilities.LexicalAnalyzer;
import app.compiler_project.utilities.ResultsPackage;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

public class CompilerController {

    private final String buttonReleasedStyle = "-fx-background-color: #fc625d; -fx-background-radius: 7px; -fx-border-color: black; -fx-border-radius: 5px;";
    private final String buttonPressedStyle = "-fx-background-color: gray; -fx-background-radius: 7px; -fx-border-color: black; -fx-border-radius: 5px;";

    private ResultsPackage resultsPackage;
    private LexicalAnalyzer lexicalAnalyzer;

    @FXML
    private TextArea codeSegment;

    @FXML
    private TextArea resultArea;

    @FXML
    private TableView<Pair<Integer, String>> keywordsTable;

    @FXML
    private TableView<Pair<Integer, String>> separatorsTable;

    @FXML
    private TableView<Pair<Integer, String>> identifiersTable;

    @FXML
    private TableView<Pair<Integer, String>> constantsTable;

    @FXML
    void initialize() {
        fillTable(keywordsTable);
        fillTable(separatorsTable);
        fillTable(identifiersTable);
        fillTable(constantsTable);

        resultsPackage = new ResultsPackage(keywordsTable, separatorsTable,
                identifiersTable, constantsTable, resultArea);
        lexicalAnalyzer = new LexicalAnalyzer(resultsPackage);
    }

    @FXML
    void compileClick(MouseEvent event) {
        try {
            lexicalAnalyzer.analyze(codeSegment.getText());
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
        button.setStyle(buttonPressedStyle);
    }

    @FXML
    void buttonReleased(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(buttonReleasedStyle);
    }

    private void fillTable(TableView<Pair<Integer, String>> table) {
        TableColumn<Pair<Integer, String>, Integer> codeColumn = new TableColumn<>("code");
        codeColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getKey()));
        codeColumn.setResizable(false);
        codeColumn.setEditable(false);
        codeColumn.setSortable(false);
        codeColumn.setPrefWidth(75);
        table.getColumns().add(codeColumn);

        TableColumn<Pair<Integer, String>, String> valueColumn = new TableColumn<>("value");
        valueColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getValue()));
        valueColumn.setResizable(false);
        valueColumn.setEditable(false);
        valueColumn.setSortable(false);
        valueColumn.setPrefWidth(158);
        table.getColumns().add(valueColumn);

        table.setStyle("-fx-font-size: 20");
    }
}