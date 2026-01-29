package fx.customerPage;

import java.io.IOException;

import backend.models.Customer;
import fx.Utils;
import fx.assignMeter.AssignMeterController;
import fx.enterMeterReading.EnterMeterReadingController;
import fx.generateInvoice.GenerateInvoiceController;
import fx.manageInvoices.ManageInvoicesController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CustomerPageController {

    private Customer selectedCustomer;

    @FXML
    private Label label;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneLabel;

    public void setSelectedCustomer(Customer customer) {
        this.selectedCustomer = customer;
        nameLabel.setText("Name: " + customer.getName());
        emailLabel.setText("Email: " + customer.getEmail());
        phoneLabel.setText("Phone Number: " + customer.getPhoneNumber());
        label.setText(customer.getId());
    }

    @FXML
    public void onExit(ActionEvent event) {
        Utils.navigate("manageCustomers/manageCustomers.fxml", event, "Manage Customers");
    }

    @FXML
    public void onGenerateInvoice(ActionEvent event) {
        Utils.navigate("generateInvoice/generateInvoice.fxml", event, "Generate Invoice", loader -> {
            GenerateInvoiceController controller = loader.getController();
            controller.setSelectedCustomer(selectedCustomer);
        });
    }

    @FXML
    public void onViewInvoices(ActionEvent event) {
        Utils.navigate("manageInvoices/manageInvoices.fxml", event, "Manage Invoices", loader -> {
            ManageInvoicesController controller = loader.getController();
            controller.setSelectedCustomer(selectedCustomer);
        });
    }

    @FXML
    public void onAssignMeter(ActionEvent event) {
        Utils.navigate("assignMeter/assignMeter.fxml", event, "Assign Meter", loader -> {
            AssignMeterController controller = loader.getController();
            controller.setSelectedCustomer(selectedCustomer);
        });
    }

    @FXML
    public void onEnterMeterReading(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../enterMeterReading/EnterMeterReading.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EnterMeterReadingController controller = loader.getController();
        controller.setSelectedCustomer(selectedCustomer);

        Stage dialog = new Stage();
        dialog.setTitle("Enter Meter Reading");
        dialog.initOwner(label.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

}
