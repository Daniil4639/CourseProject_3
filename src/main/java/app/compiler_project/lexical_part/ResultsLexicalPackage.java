package app.compiler_project.lexical_part;

import javafx.util.Pair;
import java.util.List;

public record ResultsLexicalPackage(
        List<Pair<Integer, String>> keywordsTable,
        List<Pair<Integer, String>> separatorsTable,
        List<Pair<Integer, String>> identifiersTable,
        List<Pair<Integer, String>> constantsTable,
        List<Pair<String, Pair<Integer, String>>> resultArea) {

    public void clearResults() {
        keywordsTable.clear();
        separatorsTable.clear();
        identifiersTable.clear();
        constantsTable.clear();
        resultArea.clear();
    }

    public Pair<Integer, String> addToken(List<Pair<Integer, String>> table, String token) {
        int index = -1;
        for (Pair<Integer, String> pair: table) {
            if (token.equals(pair.getValue())) {
                index = pair.getKey();
                break;
            }
        }

        if (index == -1) {
            table.add(new Pair<>(table.size() + 1, token));
            return table.getLast();
        }
        else {
            return table.get(index - 1);
        }
    }

    public String getResultText() {
        StringBuilder builder = new StringBuilder();

        for (Pair<String, Pair<Integer, String>> elem: resultArea) {
            builder.append("(").append(elem.getKey()).append(", ").append(elem.getValue().toString())
                    .append("),");
        }

        return builder.substring(0, builder.length() - 1);
    }
}
