package app.compiler_project.assembler_part;

import app.compiler_project.lexical_part.ResultsLexicalPackage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AssemblerTransformator {

    private final ResultsLexicalPackage lexicalPackage;
    private final List<String> poliz;
    private final StringBuilder builder;

    public AssemblerTransformator(ResultsLexicalPackage lexicalPackage, List<String> poliz) {
        this.lexicalPackage = lexicalPackage;
        this.poliz = poliz;
        builder = new StringBuilder();
    }

    public String getString() {
        return builder.toString();
    }

    public void transformToAssembler() {
        builder.setLength(0);

        builder.append(".model small").append(System.lineSeparator()).append(System.lineSeparator());
        builder.append(".stack 100h").append(System.lineSeparator()).append(System.lineSeparator());

        initializeData();
        initializeCode();
    }

    private void initializeData() {
        builder.append(".data").append(System.lineSeparator());

        for (String id: lexicalPackage.identifiersTable().stream().map(Pair::getValue).toList()) {
            builder.append("\t").append(id).append(" DW ?").append(System.lineSeparator());
        }
        builder.append(System.lineSeparator()).append(dataPreps).append(System.lineSeparator());
    }

    private void initializeCode() {
        builder.append(".code").append(System.lineSeparator());
        builder.append("begin:").append(System.lineSeparator());
        builder.append("\tmov ax, @data").append(System.lineSeparator());
        builder.append("\tmov ds, ax").append(System.lineSeparator()).append(System.lineSeparator());

        transformPoliz();

        builder.append(exitCommands);
        builder.append(System.lineSeparator());
        builder.append(inputOutputFunc);
    }

    private void transformPoliz() {
        List<Integer> marks = new ArrayList<>();
        for (int i = 0; i < poliz.size(); i++) {
            if (poliz.get(i).equals("!F") || poliz.get(i).equals("!")) {
                marks.add(Integer.parseInt(poliz.get(i - 1)));
            }
        }
        marks.sort(Integer::compareTo);
        marks.add(-1);
        int markIndex = 0;
        int conditionsCount = 0;

        Stack<String> operands = new Stack<>();
        for (int i = 0; i < poliz.size(); i++) {
            if (i == marks.get(markIndex)) {
                builder.append("mark_").append(++markIndex).append(":");
                builder.append(System.lineSeparator()).append(System.lineSeparator());
            }

            switch (poliz.get(i)) {
                case "input" -> {
                    addInput(operands.pop());
                }
                case "output" -> {
                    addOutput(operands.pop());
                }
                case "let" -> {
                    addLet(operands.pop(), operands.pop());
                }
                case "+" -> {
                    addArOperation(operands.pop(), operands.pop(), "add");
                    operands.add("1");
                }
                case "-" -> {
                    addArOperation(operands.pop(), operands.pop(), "sub");
                    operands.add("1");
                }
                case "*" -> {
                    addArOperation(operands.pop(), operands.pop(), "imul");
                    operands.add("1");
                }
                case "/" -> {
                    addArOperation(operands.pop(), operands.pop(), "div");
                    operands.add("1");
                }
                case ">" -> {
                    checkBigger(operands.pop(), operands.pop(), conditionsCount);
                    conditionsCount += 2;
                    operands.add("1");
                }
                case "<" -> {
                    checkLess(operands.pop(), operands.pop(), conditionsCount);
                    conditionsCount += 2;
                    operands.add("1");
                }
                case "==" -> {
                    checkEqual(operands.pop(), operands.pop(), conditionsCount);
                    conditionsCount += 2;
                    operands.add("1");
                }
                case "!=" -> {
                    checkNotEqual(operands.pop(), operands.pop(), conditionsCount);
                    conditionsCount += 2;
                    operands.add("1");
                }
                case "!" -> {
                    findMark(operands.pop(), marks);
                }
                case "!F" -> {
                    findMarkWithCondition(operands.pop(), operands.pop(), marks);
                }
                default -> {
                    operands.push(poliz.get(i));

                    if (poliz.get(i + 1).equals("!") || poliz.get(i + 1).equals("!F")) {
                        continue;
                    }

                    try {
                        int num = Integer.parseInt(poliz.get(i));
                        builder.append("\tpush #").append(num).append(System.lineSeparator());
                        builder.append(System.lineSeparator());
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }

    private void addInput(String operand) {
        builder.append("\tmov dx, offset n").append(System.lineSeparator());
        builder.append("\tcall input").append(System.lineSeparator());
        builder.append("\tcall str2int").append(System.lineSeparator());
        builder.append("\tmov ").append(operand).append(", ax").append(System.lineSeparator());
        builder.append(System.lineSeparator());
    }

    private void addOutput(String operand) {
        try {
            Integer.parseInt(operand);
            builder.append("\tpop ax").append(System.lineSeparator());
        } catch (NumberFormatException ex) {
            builder.append("\tmov ax, ").append(operand).append(System.lineSeparator());
        }

        builder.append("\tcall int2str").append(System.lineSeparator());
        builder.append("\tcall output").append(System.lineSeparator()).append(System.lineSeparator());
    }

    private void addLet(String value, String identifier) {
        try {
            Integer.parseInt(value);
            builder.append("\tpop ax").append(System.lineSeparator());
            builder.append("\tmov ").append(identifier).append(", ax");
        } catch (NumberFormatException ex) {
            builder.append("\tmov ").append(identifier).append(", ").append(value);
        }

        builder.append(System.lineSeparator()).append(System.lineSeparator());
    }

    private void addArOperation(String val2, String val1, String operation) {
        check2Values(val2, val1);

        if (operation.equals("div")) {
            builder.append("\tdiv bx").append(System.lineSeparator());
            builder.append("\tpush al").append(System.lineSeparator()).append(System.lineSeparator());
        }
        else {
            builder.append("\t").append(operation).append(" ax, bx").append(System.lineSeparator());
            builder.append("\tpush ax").append(System.lineSeparator()).append(System.lineSeparator());
        }
    }

    private void checkBigger(String val2, String val1, int count) {
        check2Values(val2, val1);

        builder.append("\tsub val1, val2").append(System.lineSeparator());
        builder.append("\tb.eq condition_").append(count + 1).append(System.lineSeparator());
        builder.append("\tb.mi condition_").append(count + 1).append(System.lineSeparator());
        builder.append("\tpush #1").append(System.lineSeparator());
        builder.append("\tb condition_").append(count + 2).append(System.lineSeparator());
        builder.append("condition_").append(count + 1).append(":").append(System.lineSeparator());
        builder.append("\tpush #0").append(System.lineSeparator());
        builder.append("condition_").append(count + 2).append(":").append(System.lineSeparator());
        builder.append(System.lineSeparator());
    }

    private void checkLess(String val2, String val1, int count) {
        check2Values(val2, val1);

        builder.append("\tsub val1, val2").append(System.lineSeparator());
        builder.append("\tb.mi condition_").append(count + 1).append(System.lineSeparator());
        builder.append("\tpush #0").append(System.lineSeparator());
        builder.append("\tb condition_").append(count + 2).append(System.lineSeparator());
        builder.append("condition_").append(count + 1).append(":").append(System.lineSeparator());
        builder.append("\tpush #1").append(System.lineSeparator());
        builder.append("condition_").append(count + 2).append(":").append(System.lineSeparator());
        builder.append(System.lineSeparator());
    }

    private void checkEqual(String val2, String val1, int count) {
        check2Values(val2, val1);

        builder.append("\tsub val1, val2").append(System.lineSeparator());
        builder.append("\tb.eq condition_").append(count + 1).append(System.lineSeparator());
        builder.append("\tpush #0").append(System.lineSeparator());
        builder.append("\tb condition_").append(count + 2).append(System.lineSeparator());
        builder.append("condition_").append(count + 1).append(":").append(System.lineSeparator());
        builder.append("\tpush #1").append(System.lineSeparator());
        builder.append("condition_").append(count + 2).append(":").append(System.lineSeparator());
        builder.append(System.lineSeparator());
    }

    private void checkNotEqual(String val2, String val1, int count) {
        check2Values(val2, val1);

        builder.append("\tsub val1, val2").append(System.lineSeparator());
        builder.append("\tb.eq condition_").append(count + 1).append(System.lineSeparator());
        builder.append("\tpush #1").append(System.lineSeparator());
        builder.append("\tb condition_").append(count + 2).append(System.lineSeparator());
        builder.append("condition_").append(count + 1).append(":").append(System.lineSeparator());
        builder.append("\tpush #0").append(System.lineSeparator());
        builder.append("condition_").append(count + 2).append(":").append(System.lineSeparator());
        builder.append(System.lineSeparator());
    }

    private void check2Values(String val2, String val1) {
        boolean ok1 = false;
        boolean ok2 = false;
        try {
            Integer.parseInt(val1);
            ok1 = true;
        } catch (NumberFormatException ignored) {}
        try {
            Integer.parseInt(val2);
            ok2 = true;
        } catch (NumberFormatException ignored) {}

        if (ok1 && ok2) {
            builder.append("\tpop bx").append(System.lineSeparator());
            builder.append("\tpop ax").append(System.lineSeparator());
        }
        else if (ok1) {
            builder.append("\tmov bx, ").append(val2).append(System.lineSeparator());
            builder.append("\tpop ax").append(System.lineSeparator());
        }
        else if (ok2) {
            builder.append("\tmov ax, ").append(val1).append(System.lineSeparator());
            builder.append("\tpop bx").append(System.lineSeparator());
        }
        else {
            builder.append("\tmov ax, ").append(val1).append(System.lineSeparator());
            builder.append("\tmov bx, ").append(val2).append(System.lineSeparator());
        }
    }

    private void findMark(String num, List<Integer> list) {
        Integer n = Integer.parseInt(num);

        for (int i = 0; i < list.size(); i++) {
            if (n.equals(list.get(i))) {
                builder.append("\tb mark_").append(i + 1).append(System.lineSeparator())
                        .append(System.lineSeparator());
                return;
            }
        }
    }

    private void findMarkWithCondition(String num, String condition, List<Integer> list) {
        Integer n = Integer.parseInt(num);

        try {
            Integer.parseInt(condition);
            builder.append("\tpop ax").append(System.lineSeparator());
        } catch (NumberFormatException ex) {
            builder.append("\tmov ax, ").append(condition).append(System.lineSeparator());
        }

        builder.append("\tsub ax, #0").append(System.lineSeparator());
        for (int i = 0; i < list.size(); i++) {
            if (n.equals(list.get(i))) {
                builder.append("\tb.eq mark_").append(i + 1).append(System.lineSeparator())
                        .append(System.lineSeparator());
                return;
            }
        }
    }

    private static final String dataPreps = """
            \tn db 5
            \tnlength db 0
            \tncontents db 5 dup (?)
            
            \tbuff db 100 dup(0),'$'
            \ttable1 db '0123456789ABCDEF'
            
            \tbase dw 10
            """;

    private static final String inputOutputFunc = """
            output PROC
            \tmov ah,9
            \tint 21h
            \tret
            output ENDP
            
            ncontents input PROC
            \tmov ah,0Ah
            \tint 21h
            \tret
            input ENDP
            
            str2int PROC
            \txor di,di
            \txor ax,ax
            \tmov cl,nlength
            \txor ch,ch
            \txor bx,bx
            \tmov si,cx
            \tmov cl,10
            next1:
            \tmov bl,byte ptr ncontents[di]
            \tsub bl,'0'
            \tjb error1
            \tcmp bl,9
            \tja error1
            \tmul cx
            \tadd ax,bx
            \tinc di
            \tcmp di,si
            \tjb next1
            \tret
            error1:
            \tmov dx,offset mess4
            \tmov ah,9
            \tint 21h
            \tjmp exit
            str2int ENDP
            
            int2str PROC
            \txor di,di
            \tmov cx,99
            zeroizing:
            \tmov byte ptr buff[di],0
            \tinc di
            loop zeroizing
            \txor di,di
            \tmov di,offset buff+99
            next2:
            \txor dx,dx
            \tdiv base
            \tmov si,offset table1
            \tadd si,dx
            \tmov dl,[si]
            \tmov [di],dl
            \tdec di
            \tcmp ax,0
            \tjnz next2
            \tmov dx,di
            \tret
            int2str ENDP
            """;

    private static final String exitCommands = """
            exit:
            \tmov ax,4C00h
            \tint 21h
            end begin
            """;
}