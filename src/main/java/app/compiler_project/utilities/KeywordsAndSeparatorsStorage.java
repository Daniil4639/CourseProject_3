package app.compiler_project.utilities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeywordsAndSeparatorsStorage {

    public static Set<String> KEYWORDS = new HashSet<>(List.of(
            "program",
            "var",
            "begin",
            "end",
            "longint",
            "let",
            "if",
            "then",
            "else",
            "for",
            "to",
            "step",
            "do",
            "downto",
            "while",
            "input",
            "output"));

    public static Set<String> SEPARATORS = new HashSet<>(List.of(
            ";",
            ",",
            "{",
            "}",
            "(",
            ")",
            "=",
            "-",
            "+",
            ">",
            "<",
            ">=",
            "<=",
            "*",
            "/"));
}