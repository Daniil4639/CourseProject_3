package app.compiler_project.lexical_part;

import javafx.util.Pair;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LexicalAnalyzer {

    private final String regexNumber = "(-)?[0-9]+";
    private final String regexIdentifier = "[a-zA-Z][0-9][a-zA-Z]+";

    private final ResultsLexicalPackage resultsPackage;

    public LexicalAnalyzer(ResultsLexicalPackage resultsPackage) {
        this.resultsPackage = resultsPackage;
    }

    public void analyze(String code) throws IllegalArgumentException {
        code = deleteComments(code);

        for (String symbol: KeywordsAndSeparatorsStorage.SEPARATORS) {
            code = code.replace(symbol, " " + symbol + " ");
        }

        resultsPackage.clearResults();

        resultsPackage.resultArea().addAll(Stream.of(code.split("(\\s)+|(\\r?\\n)+"))
                .map(this::checkToken).toList());
    }

    private String deleteComments(String code) {
        return code.replaceAll("\\(\\*.*\\*\\)", "");
    }

    private Pair<String, Pair<Integer, String>> checkToken(String token)
            throws IllegalArgumentException {

        List<Pair<Integer, String>> table;
        String tableName;

        if (KeywordsAndSeparatorsStorage.KEYWORDS.contains(token)) {
            table = resultsPackage.keywordsTable();
            tableName = "Keywords";
        }
        else if (KeywordsAndSeparatorsStorage.SEPARATORS.contains(token)
                || KeywordsAndSeparatorsStorage.OPERATORS.contains(token)) {

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

        return new Pair<>(tableName, resultsPackage.addToken(table, token));
    }
}