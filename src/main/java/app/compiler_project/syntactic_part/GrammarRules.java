package app.compiler_project.syntactic_part;

import java.util.ArrayList;
import java.util.List;

public class GrammarRules {

    public final static List<List<String>> rules = List.of(
            List.of("program", "var", "E", "begin", "E", "end"),
            List.of("E", ";", "E"),
            List.of("E", "i"),
            List.of("E", "i", "E"),
            List.of(",", "i"),
            List.of(",", "i", "E"),
            List.of("longint"),
            List.of("{", "E", "}"),
            List.of("let", "i", "=", "(", "E", ")"),
            List.of("if", "E", "then", "E"),
            List.of("if", "E", "then", "E", "else", "E"),
            List.of("for", "E", "to", "E", "do", "E"),
            List.of("for", "E", "downto", "E", "do", "E"),
            List.of("E", "step", "E"),
            List.of("while", "(", "E", ")", "E"),
            List.of("input", "(", "E", ")"),
            List.of("i"),
            List.of("i", ",", "E"),
            List.of("output", "(", "E", ")"),
            List.of("E", ",", "E"),
            List.of("E", "b", "E"),
            List.of("n")
    );

    public static void zipStack(List<String> stack, String endToken) throws IllegalArgumentException {
        int zipCnt = 0;

        while(zip(stack)) {
            zipCnt++;
            if (endToken.equals("else")) {
                break;
            }
        }

        if (zipCnt == 0) {
            throw new IllegalArgumentException(
                    "Incorrect terminal sequence!\nZipping isn't possible!\n" + stack
            );
        }
    }

    private static boolean zip(List<String> stack) {
        List<String> elements = new ArrayList<>();

        for (int i = stack.size() - 1; i >= 0; i--) {
            elements.addFirst(stack.get(i));

            for (List<String> rule: rules) {
                if (compareRules(elements, rule)) {
                    zipPartOfStack(stack, elements.size());
                    return true;
                }
            }
        }

        return false;
    }

    private static void zipPartOfStack(List<String> stack, int cnt) {
        while (cnt > 0) {
            stack.removeLast();
            cnt--;
        }

        if (!stack.getLast().equals("E")) {
            stack.add("E");
        }
    }

    private static boolean compareRules(List<String> elements, List<String> rule) {
        if (elements.size() != rule.size()) {
            return false;
        }

        for (int i = 0; i < elements.size(); i++) {
            if (!elements.get(i).equals(rule.get(i))) {
                return false;
            }
        }

        return true;
    }
}
