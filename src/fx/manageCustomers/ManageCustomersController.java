package fx.manageCustomers;

import java.util.List;

import backend.models.Customer;
import fx.Utils;
import fx.customerPage.CustomerPageController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageCustomersController {

    @FXML
    private Button addCustomerButton;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> idColumn;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> emailColumn;

    @FXML
    private TableColumn<Customer, String> phoneColumn;

    @FXML
    private TableColumn<Customer, String> addressColumn;

    @FXML
    private TextField searchCustomerField;

    public List<Customer> getCustomers() {
        return Customer.objects.findAll();
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        customerTable.setItems(FXCollections.observableArrayList(getCustomers()));

        customerTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Customer selected = customerTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openCustomerDetails(selected, e);
                }
            }
        });

        searchCustomerField.textProperty().addListener((observable, oldValue, newValue) -> {
            onSearchCustomer();
        });
    }

    private void openCustomerDetails(Customer customer, Event event) {
        Utils.navigate("customerPage/customerPage.fxml", event, "Customer Details",
                loader -> {
                    CustomerPageController controller = loader.getController();
                    controller.setSelectedCustomer(customer);
                });

    }

    private void onSearchCustomer() {
        if (searchCustomerField.getText().isEmpty()) {
            customerTable.setItems(FXCollections.observableArrayList(getCustomers()));
        } else {
            String searchTerm = searchCustomerField.getText();
            List<Customer> filtered = Customer.objects.search(searchTerm);
            customerTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    @FXML
    private void onAddCustomer(ActionEvent event) {
        Utils.navigate("registerCustomer/registerCustomer.fxml", event, "Register Customer");
    }

    public void onBack(ActionEvent event) {
        // Navigate back to dashboard
        Utils.navigate("dashboard/dashboard.fxml", event, "Dashboard");
    }

}
