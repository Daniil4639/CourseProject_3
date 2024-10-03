package app.compiler_project.utilities;

import javafx.scene.control.TableView;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

    private final String regexNumber = "(-)?[0-9]+";
    private final String regexIdentifier = "[a-zA-Z][0-9][a-zA-Z]+";

    private final ResultsPackage resultsPackage;

    public LexicalAnalyzer(ResultsPackage resultsPackage) {
        this.resultsPackage = resultsPackage;
    }

    public void analyze(String code) throws IllegalArgumentException {
        code = deleteComments(code);

        for (String symbol: KeywordsAndSeparatorsStorage.SEPARATORS) {
            code = code.replace(symbol, " " + symbol + " ");
        }

        resultsPackage.clearResults();

        List<String> tokens = new ArrayList<>(List.of(code.split("(\\s)+|(\\r?\\n)+")))
                .stream().map(this::checkToken).map(data -> "(" + data + ")").toList();

        StringBuilder stringBuilder = new StringBuilder();
        tokens.forEach(data -> stringBuilder.append(data).append("; "));

        resultsPackage.resultArea().setText(stringBuilder.substring(0, stringBuilder.length() - 1));
    }

    private String deleteComments(String code) {
        return code.replaceAll("\\(\\*.*\\*\\)", "");
    }

    private String checkToken(String token) throws IllegalArgumentException {
        TableView<Pair<Integer, String>> table = null;
        String tableName;

        if (KeywordsAndSeparatorsStorage.KEYWORDS.contains(token)) {
            table = resultsPackage.keywordsTable();
            tableName = "Keywords";
        }
        else if (KeywordsAndSeparatorsStorage.SEPARATORS.contains(token)) {
            table = resultsPackage.separatorsTable();
            tableName = "Separators";
        }
        else if (Pattern.matches(regexNumber, token)) {
            table = resultsPackage.constantsTable();
            tableName = "Constants";
        }
        else if (Pattern.matches(regexIdentifier, token)) {
            table = resultsPackage.identifiersTable();
            tableName = "Identifier";
        }
        else {
            throw new IllegalArgumentException("Unknown token: \"" + token + "\"");
        }

        return tableName + ", " + resultsPackage.addToken(table, token);
    }
}