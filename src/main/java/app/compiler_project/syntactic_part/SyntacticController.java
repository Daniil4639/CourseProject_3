package app.compiler_project.syntactic_part;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.List;

public class SyntacticController {

    @FXML
    private TextArea resultArea;

    public void setResult(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String str: list) {
            builder.append(str).append(System.lineSeparator());
        }

        resultArea.setText(builder.substring(0, builder.length() - 1));
    }
}
