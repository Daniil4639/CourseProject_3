package app.compiler_project.syntactic_part;

import app.compiler_project.lexical_part.KeywordsAndSeparatorsStorage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class SyntacticAnalyzer {

    public List<String> analyze(List<Pair<String, Pair<Integer, String>>> lexicalResults)
            throws IllegalArgumentException {

        List<String> res = new ArrayList<>();

        List<String> stack = new ArrayList<>();
        stack.add("⟂");
        res.add(stack.toString());

        for (Pair<String, Pair<Integer, String>> elem: lexicalResults) {
            String elemProjection = getProjection(elem);

            int relation = OperatorMatrix
                    .getTerminalRelation(stack.get(getIndexOfFirstTerm(stack)), elemProjection);

            if (relation == -1 || relation == 0) {
                stack.add(elemProjection);
            }
            else {
                GrammarRules.zipStack(stack, elemProjection);

                stack.add(elemProjection);
            }

            res.add(stack.toString());
        }

        GrammarRules.zipStack(stack, "");
        res.add(stack.toString());

        stack.add("⟂");
        res.add(stack.toString());

        return res;
    }

    private int getIndexOfFirstTerm(List<String> list) throws IllegalArgumentException {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (!list.get(i).equals("E")) {
                return i;
            }
        }

        throw new IllegalArgumentException("Incorrect terminal sequence!\nNot terminal in stack!");
    }

    private String getProjection(Pair<String, Pair<Integer, String>> elem) {
        if (elem.getKey().equals("Constants")) {
            return  "n";
        }
        else if (elem.getKey().equals("Identifier")) {
            return  "i";
        }
        else if (elem.getKey().equals("Separators")) {
            if (KeywordsAndSeparatorsStorage.OPERATORS.contains(elem.getValue().getValue())) {
                return  "b";
            }
            else {
                return elem.getValue().getValue();
            }
        }
        else {
            return elem.getValue().getValue();
        }
    }
}
