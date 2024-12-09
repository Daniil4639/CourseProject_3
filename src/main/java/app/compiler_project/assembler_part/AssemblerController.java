package app.compiler_project.assembler_part;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.List;

public class AssemblerController {

    @FXML
    private TextArea resultArea;

    public void setResult(String result) {
        resultArea.setText(result);
    }
}
