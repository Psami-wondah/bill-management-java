package fx.dashboard;

import fx.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {

    @FXML
    private Button registerCustomerButton;

    @FXML
    private Button manageTariffsButton;

    @FXML
    private Button manageCustomersButton;

    @FXML
    private Button exitButton;

    public void initialize() {
        registerCustomerButton.setOnAction(event -> {
            Utils.navigate("registerCustomer/registerCustomer.fxml", event, "Register Customer");
        });

        exitButton.setOnAction(event -> {
            Utils.navigate("home/home.fxml", event, "Billing Login");
        });

        manageTariffsButton.setOnAction(event -> {
            Utils.navigate("manageTariffs/manageTariffs.fxml", event, "Manage Tariffs");
        });

        manageCustomersButton.setOnAction(event -> {
            Utils.navigate("manageCustomers/manageCustomers.fxml", event, "Manage Customers");
        });

    }

}
