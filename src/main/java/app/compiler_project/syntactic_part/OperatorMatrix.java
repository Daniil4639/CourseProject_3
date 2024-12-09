package app.compiler_project.syntactic_part;

import java.util.Map;

public class OperatorMatrix {

    public static int getTerminalRelation(String left, String right) {
        int indexLeft = terminalIndexMap.get(left);
        int indexRight = terminalIndexMap.get(right);

        Integer result = terminalMatrix[indexLeft][indexRight];
        if (result == null) {
            throw new IllegalArgumentException("Incorrect relation:\n" + left + " -> " + right);
        }

        return result;
    }

    private final static Map<String, Integer> terminalIndexMap = Map.ofEntries(
            Map.entry("program", 0), Map.entry("var", 1),
            Map.entry("begin", 2), Map.entry(";", 3),
            Map.entry("end", 4), Map.entry(",", 5),
            Map.entry("longint", 6), Map.entry("{", 7),
            Map.entry("}", 8), Map.entry("(", 9),
            Map.entry(")", 10), Map.entry("=", 11),
            Map.entry("let", 12), Map.entry("if", 13),
            Map.entry("then", 14), Map.entry("else", 15),
            Map.entry("for", 16), Map.entry("to", 17),
            Map.entry("step", 18), Map.entry("do", 19),
            Map.entry("downto", 20), Map.entry("while", 21),
            Map.entry("input", 22), Map.entry("output", 23),
            Map.entry("i", 24), Map.entry("n", 25),
            Map.entry("b", 26), Map.entry("⟂", 27));

    private final static Integer[][] terminalMatrix = new Integer[][] {
            {null, 0, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, -1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, -1, null, null, null},
            {null, null, null, -1, null, null, null, -1, null, null, null, null, -1, -1, null, null, -1, null, null, null, null, -1, -1, -1, null, null, null, null},
            {null, null, null, -1, 1, null, null, -1, 1, null, null, null, -1, -1, null, null, -1, null, null, null, null, -1, -1, -1, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 1},
            {null, null, null, null, null, -1, null, null, null, null, 1, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, -1, -1, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 1, null, null, null},
            {null, null, null, -1, null, null, null, -1, null, null, null, null, -1, -1, null, null, -1, null, null, null, null, -1, -1, -1, null, null, null, null},
            {null, null, null, 1, 1, null, null, null, 1, null, null, null, null, null, null, 1, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, -1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, -1, -1, -1, null},
            {null, null, null, 1, 1, null, null, -1, 1, null, null, null, -1, -1, null, 1, -1, 1, null, null, 1, -1, -1, -1, null, null, null, null},
            {null, null, null, null, 1, null, null, null, 1, 0, null, null, null, null, null, null, null, 1, null, null, 1, null, null, null, -1, -1, -1, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, -1, -1, -1, null},
            {null, null, null, 1, 1, null, null, -1, 1, null, null, null, -1, -1, null, 1, -1, null, null, null, null, -1, -1, -1, null, null, null, null},
            {null, null, null, 1, 1, null, null, -1, 1, null, null, null, -1, -1, null, 1, -1, null, null, null, null, -1, -1, -1, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, -1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, 1, 1, null, null, null, 1, null, null, null, null, null, null, 1, null, null, -1, null, null, null, null, null, -1, -1, -1, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 1, null, null, null, null, -1, -1, -1, null},
            {null, null, null, null, null, null, null, -1, null, null, null, null, -1, -1, null, null, -1, null, null, null, null, -1, -1, -1, null, null, null, null},
            {null, null, null, 1, 1, null, null, null, 1, null, null, null, null, null, null, 1, null, null, -1, null, null, null, null, null, -1, -1, -1, null},
            {null, null, null, null, null, null, null, null, null, 0, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, 0, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, 0, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, 1, null, 1, 0, null, null, 1, 0, 1, 0, null, null, 1, null, null, 1, 1, 1, 1, null, null, null, null, null, 1, null},
            {null, null, null, null, 1, 1, null, null, 1, null, 1, null, null, null, 1, null, null, 1, 1, 1, null, null, null, null, null, null, 1, null},
            {null, null, null, null, 1, 1, null, null, 1, null, 1, null, null, null, 1, null, null, 1, 1, 1, null, null, null, null, -1, -1, -1, null},
            {-1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
    };
}
