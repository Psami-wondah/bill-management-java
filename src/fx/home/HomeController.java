package fx.home;

import java.io.IOException;

import fx.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class HomeController {
    public static final String PASSWORD = "admin";

    @FXML
    private Label label;

    @FXML
    private Button submitButton;

    @FXML
    private PasswordField passwordField;

    public void initialize() {
        submitButton.setOnAction(event -> {
            try {
                handleSubmit(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void handleSubmit(ActionEvent event) throws IOException {

        String enteredPassword = passwordField.getText();
        if (PASSWORD.equals(enteredPassword)) {
            Utils.navigate("dashboard/dashboard.fxml", event, "Billing Dashboard");
        } else {
            label.setText("Access Denied!");
        }
    }
}