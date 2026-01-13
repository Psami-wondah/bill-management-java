package fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class Controller {
    public static final String PASSWORD = "admin";

    @FXML
    private Label label;

    @FXML
    private Button submitButton;

    @FXML
    private PasswordField passwordField;

    public void initialize() {
        submitButton.setOnAction(event -> handleSubmit());

    }

    private void handleSubmit() {
        String enteredPassword = passwordField.getText();
        if (PASSWORD.equals(enteredPassword)) {
            label.setText("Access Granted!");
        } else {
            label.setText("Access Denied!");
        }
    }
}