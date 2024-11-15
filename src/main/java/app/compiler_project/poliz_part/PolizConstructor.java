package app.compiler_project.poliz_part;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class PolizConstructor {

    private final Map<String, Integer> operatorsPriority = Map.ofEntries(
            Map.entry("+", 1),
            Map.entry("-", 1),
            Map.entry("*", 2),
            Map.entry("/", 2),
            Map.entry(">", 0),
            Map.entry("<", 0),
            Map.entry("==", 0),
            Map.entry("!=", 0)
    );

    private final List<String> poliz;

    public PolizConstructor(List<String> poliz) {
        this.poliz = poliz;
    }

    public void construct(List<Pair<String, Pair<Integer, String>>> lexicalResult) throws IllegalArgumentException {
        poliz.clear();

        complexAnalyzer(lexicalResult.subList(findBeginIndex(lexicalResult) + 1,
                lexicalResult.size() - 1));
    }

    private void complexAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult) throws IllegalArgumentException {
        List<Integer> operatorIndexes = new ArrayList<>();
        operatorIndexes.add(0);

        int currIndex = 0;
        int level = 0;
        for (String token: lexicalResult.stream().map(data->data.getValue().getValue()).toList()) {
            if (token.equals(";") && level == 0) {
                operatorIndexes.add(currIndex + 1);
            }
            else if (token.equals("{")) {
                level++;
            }
            else if (token.equals("}")) {
                level--;
            }
            currIndex++;
        }

        for (int i = 0; i < operatorIndexes.size() - 1; i++) {
            List<Pair<String, Pair<Integer, String>>> currentList = lexicalResult
                    .subList(operatorIndexes.get(i), operatorIndexes.get(i + 1) - 1);

            checkComplexToken(currentList);
        }

        checkComplexToken(lexicalResult.subList(operatorIndexes.getLast(), lexicalResult.size()));
    }

    private void checkComplexToken(List<Pair<String, Pair<Integer, String>>> currentList) throws IllegalArgumentException {
        switch (currentList.getFirst().getValue().getValue()) {
            case "{" -> {
                complexAnalyzer(currentList.subList(1, currentList.size() - 1));
            }
            case "let" -> {
                letAnalyzer(currentList);
            }
            case "if" -> {
                ifAnalyzer(currentList);
            }
            case "for" -> {
                forAnalyzer(currentList);
            }
            case "while" -> {
                whileAnalyzer(currentList);
            }
            case "input" -> {
                inputAnalyzer(currentList);
            }
            case "output" -> {
                outputAnalyzer(currentList);
            }
            default -> {
                throw new IllegalArgumentException("Something's wrong: analyzer error in token \""
                        + currentList.getFirst().getValue().getValue() + "\"!");
            }
        }
    }

    private void letAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult) throws IllegalArgumentException {
        poliz.add(lexicalResult.get(1).getValue().getValue());
        sequenceAnalyzer(lexicalResult.subList(4, lexicalResult.size() - 1));
        poliz.add("let");
    }

    private void ifAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult) throws IllegalArgumentException {
        int thenIndex = -1;
        int elseIndex = -1;

        for (int i = 0; i < lexicalResult.size(); i++) {
            if (lexicalResult.get(i).getValue().getValue().equals("then")) {
                thenIndex = i;
            }
            else if (lexicalResult.get(i).getValue().getValue().equals("else")) {
                elseIndex = i;
            }
        }

        sequenceAnalyzer(lexicalResult.subList(1, thenIndex));

        int firstJmp = poliz.size();
        poliz.add("");
        poliz.add("!F");

        if (elseIndex == -1) {
            complexAnalyzer(lexicalResult.subList(thenIndex + 1, lexicalResult.size()));
            poliz.set(firstJmp, Integer.toString(poliz.size()));
        }
        else {
            complexAnalyzer(lexicalResult.subList(thenIndex + 1, elseIndex));
            int secondJmp = poliz.size();
            poliz.add("");
            poliz.add("!");
            poliz.set(firstJmp, Integer.toString(poliz.size()));

            complexAnalyzer(lexicalResult.subList(elseIndex + 1, lexicalResult.size()));
            poliz.set(secondJmp, Integer.toString(poliz.size()));
        }
    }

    private void forAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult) throws IllegalArgumentException {
        int toIndex = -1;
        int downToIndex = -1;
        int doIndex = -1;
        int stepIndex = -1;

        for (int i = 0; i < lexicalResult.size(); i++) {
            if (lexicalResult.get(i).getValue().getValue().equals("to")) {
                toIndex = i;
                break;
            }
            if (lexicalResult.get(i).getValue().getValue().equals("downto")) {
                downToIndex = i;
                break;
            }
        }
        for (int i = Math.max(toIndex, downToIndex); i < lexicalResult.size(); i++) {
            if (lexicalResult.get(i).getValue().getValue().equals("step")) {
                stepIndex = i;
                break;
            }
        }
        for (int i = Math.max(toIndex, downToIndex); i < lexicalResult.size(); i++) {
           if (lexicalResult.get(i).getValue().getValue().equals("do")) {
               doIndex = i;
               break;
           }
        }

        if (toIndex == -1) {
            forDownToAnalyzer(lexicalResult, downToIndex, doIndex, stepIndex);
        }
        else {
            forToAnalyzer(lexicalResult,toIndex, doIndex, stepIndex);
        }
    }

    private void forToAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult,
                               int toIndex, int doIndex, int stepIndex) throws IllegalArgumentException {

        letAnalyzer(lexicalResult.subList(1, toIndex));

        int firstJmp = poliz.size();
        if (stepIndex == -1) {
            sequenceAnalyzer(lexicalResult.subList(toIndex + 1, doIndex));
        }
        else {
            sequenceAnalyzer(lexicalResult.subList(toIndex + 1, stepIndex));
        }
        int secondJmp = poliz.size();
        poliz.add("");
        poliz.add("!F");

        complexAnalyzer(lexicalResult.subList(doIndex + 1, lexicalResult.size()));
        if (stepIndex != -1) {
            makeStep(lexicalResult, stepIndex, doIndex, "+");
        }
        poliz.add(Integer.toString(firstJmp));
        poliz.add("!");
        poliz.set(secondJmp, Integer.toString(poliz.size()));
    }

    private void forDownToAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult,
                               int downToIndex, int doIndex, int stepIndex) throws IllegalArgumentException {

        letAnalyzer(lexicalResult.subList(1, downToIndex));

        int firstJmp = poliz.size();
        if (stepIndex == -1) {
            sequenceAnalyzer(lexicalResult.subList(downToIndex + 1, doIndex));
        }
        else {
            sequenceAnalyzer(lexicalResult.subList(downToIndex + 1, stepIndex));
        }
        int secondJmp = poliz.size();
        poliz.add("");
        poliz.add("!F");

        complexAnalyzer(lexicalResult.subList(doIndex + 1, lexicalResult.size()));
        if (stepIndex != -1) {
            makeStep(lexicalResult, stepIndex, doIndex, "-");
        }
        poliz.add(Integer.toString(firstJmp));
        poliz.add("!");
        poliz.set(secondJmp, Integer.toString(poliz.size()));
    }

    private void makeStep(List<Pair<String, Pair<Integer, String>>> lexicalResult,
                          int stepIndex, int doIndex, String sign) {

        String identifier = lexicalResult.get(2).getValue().getValue();
        poliz.add(identifier);
        poliz.add(identifier);
        sequenceAnalyzer(lexicalResult.subList(stepIndex + 1, doIndex));
        poliz.add(sign);
        poliz.add("let");
    }

    private void whileAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult) throws IllegalArgumentException {
        int closeIndex = lexicalResult.stream()
                .map(data -> data.getValue().getValue()).toList().indexOf(")");

        int firstJmp = poliz.size();
        sequenceAnalyzer(lexicalResult.subList(2, closeIndex));
        int secondJmp = poliz.size();
        poliz.add("");
        poliz.add("!F");

        complexAnalyzer(lexicalResult.subList(closeIndex + 1, lexicalResult.size()));
        poliz.add(Integer.toString(firstJmp));
        poliz.add("!");
        poliz.set(secondJmp, Integer.toString(poliz.size()));
    }

    private void inputAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult) throws IllegalArgumentException {
        int index = 2;
        while (index < lexicalResult.size()) {
            poliz.add(lexicalResult.get(index).getValue().getValue());
            poliz.add("input");
            index += 2;
        }
    }

    private void outputAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult) throws IllegalArgumentException {
        List<Integer> outputSequencesIndexes = new ArrayList<>();
        outputSequencesIndexes.add(2);
        for (int i = 2; i < lexicalResult.size() - 1; i++) {
            if (lexicalResult.get(i).getValue().getValue().equals(",")) {
                outputSequencesIndexes.add(i + 1);
            }
        }

        for (int i = 0; i < outputSequencesIndexes.size() - 1; i++) {
            sequenceAnalyzer(lexicalResult
                    .subList(outputSequencesIndexes.get(i), outputSequencesIndexes.get(i + 1) - 1));
            poliz.add("output");
        }

        sequenceAnalyzer(lexicalResult
                .subList(outputSequencesIndexes.getLast(), lexicalResult.size() - 1));
        poliz.add("output");
    }

    private void sequenceAnalyzer(List<Pair<String, Pair<Integer, String>>> lexicalResult) {
        Stack<String> stack = new Stack<>();

        for (Pair<String, Pair<Integer, String>> elem: lexicalResult) {
            if (elem.getKey().equals("Constants") || elem.getKey().equals("Identifier")) {
                poliz.add(elem.getValue().getValue());
            }
            else {
                int currPriority = operatorsPriority.get(elem.getValue().getValue());
                while (!stack.empty()) {
                    if (operatorsPriority.get(stack.peek()) > currPriority) {
                        poliz.add(stack.pop());
                    }
                    else {
                        break;
                    }
                }
                stack.add(elem.getValue().getValue());
            }
        }

        while (!stack.empty()) {
            poliz.add(stack.pop());
        }
    }

    private int findBeginIndex(List<Pair<String, Pair<Integer, String>>> lexicalResult) throws IllegalArgumentException {
        int index = 0;

        for (Pair<String, Pair<Integer, String>> elem: lexicalResult) {
            if (elem.getValue().getValue().equals("begin")) {
                return index;
            }
            index++;
        }

        throw new IllegalArgumentException("Token \"begin\" was not found!");
    }
}
