package app.compiler_project.poliz_part;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.List;

public class PolizController {

    @FXML
    private TextArea resultArea;

    public void setResult(List<String> list) {
        resultArea.setText(list.toString());
    }
}
