package fx.assignMeter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import backend.enums.FuelType;
import backend.enums.RateType;
import backend.enums.ReadingType;
import backend.enums.RegisterType;
import backend.models.Account;
import backend.models.AccountTariff;
import backend.models.Customer;
import backend.models.Meter;
import backend.models.MeterReading;
import backend.models.Tariff;
import fx.Utils;
import fx.customerPage.CustomerPageController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class AssignMeterController {

    private Customer selectedCustomer;

    @FXML
    private Label label;

    @FXML
    private ComboBox<String> accountComboBox;

    @FXML
    private ComboBox<String> fuelTypeComboBox;

    @FXML
    private ComboBox<String> registerTypeComboBox;

    @FXML
    private DatePicker installationDateField;

    @FXML
    private ComboBox<String> tariffComboBox;

    @FXML
    public void initialize() {
        fuelTypeComboBox.getItems().addAll(
                FuelType.GAS.name(),
                FuelType.ELECTRICITY.name());

        registerTypeComboBox.getItems().addAll(
                RegisterType.SINGLE_REGISTER.name(),
                RegisterType.TWO_REGISTER.name());

        fuelTypeComboBox.onActionProperty().set(event -> {
            String selectedFuelType = fuelTypeComboBox.getValue();
            System.out.println("Selected Fuel Type: " + selectedFuelType);
            if (selectedFuelType.equals(FuelType.GAS.name())) {
                registerTypeComboBox.getItems().setAll(
                        RegisterType.SINGLE_REGISTER.name());

            } else if (selectedFuelType.equals(FuelType.ELECTRICITY.name())) {
                registerTypeComboBox.getItems().setAll(
                        RegisterType.SINGLE_REGISTER.name(),
                        RegisterType.TWO_REGISTER.name());
            }

            tariffComboBox.getItems().setAll(
                    Tariff.objects.filter(t -> t.getFuelType().equals(FuelType.valueOf(selectedFuelType))).stream()
                            .map(Tariff::toDisplay).toList());
        });

    }

    @FXML
    public void onBack(ActionEvent event) {
        // Logic to go back to the previous screen
        Utils.navigate("customerPage/customerPage.fxml", event, "Customer Page", loader -> {
            CustomerPageController controller = loader.getController();
            controller.setSelectedCustomer(selectedCustomer);
        });

    }

    public void onSubmit(ActionEvent event) {
        List<String> errors = new ArrayList<>();
        String selectedAccountStr = accountComboBox.getValue();
        String fuelTypeStr = fuelTypeComboBox.getValue();
        String registerTypeStr = registerTypeComboBox.getValue();
        String tariffStr = tariffComboBox.getValue();
        LocalDate installationDate = installationDateField.getValue();
        if (fuelTypeStr == null || fuelTypeStr.isBlank()) {
            errors.add("Fuel type is required.");
        }
        if (selectedAccountStr == null || selectedAccountStr.isBlank()) {
            errors.add("Please select an account.");
        }
        if (registerTypeStr == null || registerTypeStr.isBlank()) {
            errors.add("Register type is required.");
        }
        if (tariffStr == null || tariffStr.isBlank()) {
            errors.add("Tariff is required.");
        }
        if (installationDate == null) {
            errors.add("Installation date is required.");
        }
        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }
        FuelType fuelType = FuelType.valueOf(fuelTypeStr);
        RegisterType registerType = RegisterType.valueOf(registerTypeStr);
        String tariffId = tariffStr.split(" ")[0];
        Tariff selectedTariff = Tariff.objects.findById(tariffId).orElse(null);
        if (selectedTariff.getRateType() == RateType.TWO_RATE
                && registerType != RegisterType.TWO_REGISTER) {
            errors.add("Meter register type does not support dual rate tariff. Please choose again.");

        }
        if (selectedTariff.getEffectiveFrom().isAfter(installationDate)) {
            errors.add("Tariff is not effective from the meter installation date: "
                    + installationDate.toString() + ". Please choose again.");

        }
        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }
        Account selectedAccount = Account.objects.findById(
                selectedAccountStr.split(" - ")[0]).orElse(null);

        if (selectedAccount == null || selectedCustomer == null) {
            errors.add("Selected account or customer not found.");
            Utils.showErrors(errors);
            return;
        }
        Meter newMeter = new Meter(selectedAccount.getId(), fuelType, registerType, installationDate);
        MeterReading initialReading;
        if (newMeter.getRegisterType() == RegisterType.SINGLE_REGISTER) {
            initialReading = new MeterReading(newMeter.getId(), installationDate, BigDecimal.ZERO,
                    ReadingType.INITIAL);
        } else {
            initialReading = new MeterReading(newMeter.getId(), installationDate, BigDecimal.ZERO, BigDecimal.ZERO,
                    ReadingType.INITIAL);
        }
        initialReading.save();
        AccountTariff newAccountTariff = new AccountTariff(selectedAccount.getId(), selectedTariff.getId(),
                newMeter.getId(),
                newMeter.getInstallationDate(), null);

        newAccountTariff.save();
        newMeter.save();
        Utils.navigate("customerPage/customerPage.fxml", event, "Customer Page", loader -> {
            CustomerPageController controller = loader.getController();
            controller.setSelectedCustomer(selectedCustomer);
        });
    }

    @FXML
    public void onCancel(ActionEvent event) {
        // Logic to cancel and go back to the previous screen
        onBack(event);
    }

    public void setSelectedCustomer(Customer customer) {
        this.selectedCustomer = customer;
        accountComboBox.getItems().addAll(
                Account.objects.filter(a -> a.getCustomerId().equals(selectedCustomer.getId())).stream()
                        .map(Account::toDisplay).toList());
        label.setText("Assigning meter for: " + customer.getId());
    }

}
