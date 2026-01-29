package fx.manageInvoices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import backend.enums.InvoiceStatus;
import backend.models.Account;
import backend.models.Customer;
import backend.models.Invoice;
import fx.Utils;
import fx.customerPage.CustomerPageController;
import fx.generateInvoice.GenerateInvoiceController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageInvoicesController {
    private Customer selectedCustomer;

    @FXML
    private Label label;

    @FXML
    private Button generateInvoiceButton;

    @FXML
    private ComboBox<String> accountComboBox;

    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private TableColumn<Invoice, String> idColumn;

    @FXML
    private TableColumn<Invoice, String> accountIdColumn;

    @FXML
    private TableColumn<Invoice, String> accountTariffIdColumn;

    @FXML
    private TableColumn<Invoice, BigDecimal> subTotalColumn;

    @FXML
    private TableColumn<Invoice, BigDecimal> vatColumn;

    @FXML
    private TableColumn<Invoice, BigDecimal> totalColumn;

    @FXML
    private TableColumn<Invoice, InvoiceStatus> statusColumn;

    @FXML
    private TableColumn<Invoice, LocalDate> periodStartColumn;

    @FXML
    private TableColumn<Invoice, LocalDate> periodEndColumn;

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        accountIdColumn.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        accountTariffIdColumn.setCellValueFactory(new PropertyValueFactory<>("accountTariffId"));
        subTotalColumn.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        vatColumn.setCellValueFactory(new PropertyValueFactory<>("vat"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        periodStartColumn.setCellValueFactory(new PropertyValueFactory<>("periodStart"));
        periodEndColumn.setCellValueFactory(new PropertyValueFactory<>("periodEnd"));

        setupFormatting();

        accountComboBox.setOnAction(event -> {
            String selectedAccountDisplay = accountComboBox.getSelectionModel().getSelectedItem();
            if (selectedAccountDisplay != null) {
                String accountId = selectedAccountDisplay.split(" - ")[0];
                invoiceTable.setItems(
                        FXCollections
                                .observableArrayList(Invoice.objects.filter(i -> i.getAccountId().equals(accountId))));
            }
        });

        invoiceTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openInvoice(selected);
                }
            }
        });

    }

    public void setSelectedCustomer(Customer customer) {
        this.selectedCustomer = customer;
        accountComboBox.getItems().addAll(
                Account.objects.filter(a -> a.getCustomerId().equals(selectedCustomer.getId())).stream()
                        .map(Account::toDisplay).toList());
        label.setText("Viewing invoices for: " + customer.getId());

    }

    public void setActiveAccount(String accountId) {
        for (String accDisplay : accountComboBox.getItems()) {
            if (accDisplay.startsWith(accountId + " - ")) {
                accountComboBox.getSelectionModel().select(accDisplay);
                accountComboBox.getOnAction().handle(null);
                break;
            }
        }
    }

    private void openInvoice(Invoice invoice) {
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Invoice ID: " + invoice.getId());

        infoMessages.add("Invoice Summary:");
        infoMessages.add("Days: " + ChronoUnit.DAYS.between(invoice.getPeriodStart(), invoice.getPeriodEnd()));
        infoMessages.add("Subtotal: £" + invoice.getSubTotal().setScale(2, RoundingMode.HALF_UP));
        infoMessages.add("VAT: £" + invoice.getVat().setScale(2, RoundingMode.HALF_UP));
        infoMessages.add("Total: £" + invoice.getTotal().setScale(2, RoundingMode.HALF_UP));
        Utils.showInfo("Invoice Summary", infoMessages);
    }

    private void setupFormatting() {
        Utils.setupFormatter(subTotalColumn, v -> "£" + v.setScale(2, RoundingMode.HALF_UP));
        Utils.setupFormatter(vatColumn, v -> "£" + v);
        Utils.setupFormatter(totalColumn, v -> "£" + v);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Utils.setupFormatter(periodStartColumn, d -> d.format(fmt));
        Utils.setupFormatter(periodEndColumn, d -> d.format(fmt));

    }

    @FXML
    public void onGenerateInvoice(ActionEvent event) {
        Utils.navigate("generateInvoice/generateInvoice.fxml", event, "Generate Invoice", loader -> {
            GenerateInvoiceController controller = loader.getController();
            controller.setSelectedCustomer(selectedCustomer);
        });
    }

    @FXML
    public void onBack(ActionEvent event) {
        // Logic to go back to the previous screen
        Utils.navigate("customerPage/customerPage.fxml", event, "Manage Invoices", loader -> {
            CustomerPageController controller = loader.getController();
            controller.setSelectedCustomer(selectedCustomer);
        });

    }

}
