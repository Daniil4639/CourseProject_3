package app.compiler_project.utilities;

import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.Pair;

public record ResultsPackage(
        TableView<Pair<Integer, String>> keywordsTable,
        TableView<Pair<Integer, String>> separatorsTable,
        TableView<Pair<Integer, String>> identifiersTable,
        TableView<Pair<Integer, String>> constantsTable,
        TextArea resultArea) {

    public void clearResults() {
        keywordsTable.getItems().clear();
        separatorsTable.getItems().clear();
        identifiersTable.getItems().clear();
        constantsTable.getItems().clear();
        resultArea.clear();
    }

    public String addToken(TableView<Pair<Integer, String>> table, String token) {
        int index = -1;
        for (Pair<Integer, String> pair: table.getItems()) {
            if (token.equals(pair.getValue())) {
                index = pair.getKey();
                break;
            }
        }

        if (index == -1) {
            table.getItems().add(new Pair<>(table.getItems().size() + 1, token));
            return table.getItems().getLast().toString();
        }
        else {
            return table.getItems().get(index - 1).toString();
        }
    }
}
