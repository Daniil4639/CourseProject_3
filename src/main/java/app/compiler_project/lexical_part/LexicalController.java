package app.compiler_project.lexical_part;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.Pair;

public class LexicalController {

    @FXML
    private TableView<Pair<Integer, String>> constantsTable;

    @FXML
    private TableView<Pair<Integer, String>> identifiersTable;

    @FXML
    private TableView<Pair<Integer, String>> keywordsTable;

    @FXML
    public TextArea resultArea;

    @FXML
    private TableView<Pair<Integer, String>> separatorsTable;

    @FXML
    void initialize() {
        fillTable(keywordsTable);
        fillTable(separatorsTable);
        fillTable(identifiersTable);
        fillTable(constantsTable);
    }

    public void addParams(ResultsLexicalPackage resultsPackage) {
        constantsTable.getItems().clear();
        constantsTable.getItems().addAll(resultsPackage.constantsTable());

        identifiersTable.getItems().clear();
        identifiersTable.getItems().addAll(resultsPackage.identifiersTable());

        keywordsTable.getItems().clear();
        keywordsTable.getItems().addAll(resultsPackage.keywordsTable());

        separatorsTable.getItems().clear();
        separatorsTable.getItems().addAll(resultsPackage.separatorsTable());

        resultArea.setText(resultsPackage.getResultText());
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
