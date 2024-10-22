package app.compiler_project.semantic_part;

import javafx.util.Pair;

import java.util.*;

public class SemanticAnalyzer {

    public static void analyze(List<Pair<String, Pair<Integer, String>>> lexicalResults,
                               List<String> identifierTable) throws IllegalArgumentException {

        List<String> initIdentifiers = getInitIdentifiers(lexicalResults);
        checkDuplicateAndExtraInitialization(initIdentifiers, identifierTable);

        checkNullValues(lexicalResults);
    }

    private static void checkNullValues(List<Pair<String, Pair<Integer, String>>> lexicalResults)
            throws IllegalArgumentException {

        Set<String> ids = new HashSet<>();
        for (int i = getTokenIndex(lexicalResults, "begin") + 1; i < lexicalResults.size(); i++) {
            String key = lexicalResults.get(i).getKey();
            String value = lexicalResults.get(i).getValue().getValue();

            if (value.equals("input")) {
                int currIndex = i + 2;
                for (; !lexicalResults.get(currIndex).getValue().getValue().equals(")"); currIndex++) {
                    if (lexicalResults.get(currIndex).getKey().equals("Identifier")) {
                        ids.add(lexicalResults.get(currIndex).getValue().getValue());
                    }
                }
            }

            if (value.equals("let")) {
                int currIndex = i + 4;

                for (; !lexicalResults.get(currIndex).getValue().getValue().equals(")"); currIndex++) {
                    if (lexicalResults.get(currIndex).getKey().equals("Identifier")) {
                        if (!ids.contains(lexicalResults.get(currIndex).getValue().getValue())) {
                            throw new IllegalArgumentException(
                                    "Identifier " + lexicalResults.get(currIndex).getValue().getValue() +
                                            " didn't get value before it's using!"
                            );
                        }
                    }
                }

                ids.add(lexicalResults.get(i + 1).getValue().getValue());
                i = currIndex;
            }

            if (key.equals("Identifier")) {
                if (!ids.contains(value)) {
                    throw new IllegalArgumentException("Identifier " + value + " didn't get value before it's using!");
                }
            }
        }
    }

    private static void checkDuplicateAndExtraInitialization(List<String> identifiers,
                                                             List<String> identifierTable)
            throws IllegalArgumentException {

        List<String> duplicates = identifiers.stream().filter(
                elem -> Collections.frequency(identifiers, elem) > 1).toList();

        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("Duplicate initialization of: " + duplicates);
        }

        List<String> hadNotInitialize = new ArrayList<>();
        for (String id : identifierTable) {
            if (!identifiers.contains(id)) {
                hadNotInitialize.add(id);
            }
        }

        if (!hadNotInitialize.isEmpty()) {
            throw new IllegalArgumentException("Identifiers " + hadNotInitialize + " had not initialized!");
        }
    }

    private static List<String> getInitIdentifiers(List<Pair<String, Pair<Integer, String>>> list) {
        int longintIndex = getTokenIndex(list, "longint");
        int beginIndex = getTokenIndex(list, "begin");

        List<String> identifiers = new ArrayList<>();

        for (int i = longintIndex + 1; i < beginIndex; i += 2) {
            identifiers.add(list.get(i).getValue().getValue());
        }

        return identifiers;
    }

    private static int getTokenIndex(List<Pair<String, Pair<Integer, String>>> list, String token) {
        int currIndex = 0;
        for (Pair<String, Pair<Integer, String>> pair : list) {
            if (pair.getValue().getValue().equals(token)) {
                return currIndex;
            }
            currIndex++;
        }

        return -1;
    }
}
