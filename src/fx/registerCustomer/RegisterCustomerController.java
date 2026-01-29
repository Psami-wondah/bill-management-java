package fx.registerCustomer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import backend.enums.FuelType;
import backend.enums.PaymentMethod;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class RegisterCustomerController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private ComboBox<String> paymentMethodComboBox;

    @FXML
    private ComboBox<String> fuelTypeComboBox;

    @FXML
    private ComboBox<String> registerTypeComboBox;

    @FXML
    private DatePicker installationDateField;

    @FXML
    private ComboBox<String> tariffComboBox;

    public void initialize() {
        paymentMethodComboBox.getItems().addAll(
                PaymentMethod.CARD.name(),
                PaymentMethod.DIRECT_DEBIT.name(),
                PaymentMethod.CASH.name());

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

    public void onSubmit(ActionEvent event) {
        List<String> errors = new ArrayList<>();
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String paymentMethodStr = paymentMethodComboBox.getValue();
        String fuelTypeStr = fuelTypeComboBox.getValue();
        String registerTypeStr = registerTypeComboBox.getValue();
        String tariffStr = tariffComboBox.getValue();
        LocalDate installationDate = installationDateField.getValue();

        if (name.isBlank()) {
            errors.add("Name is required.");
        }
        if (email.isBlank()) {
            errors.add("Email is required.");
        }
        if (phone.isBlank()) {
            errors.add("Phone number is required.");
        }
        if (address.isBlank()) {
            errors.add("Address is required.");
        }
        if (paymentMethodStr == null || paymentMethodStr.isBlank()) {
            errors.add("Payment method is required.");
        }
        if (fuelTypeStr == null || fuelTypeStr.isBlank()) {
            errors.add("Fuel type is required.");
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
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr);
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

        Customer newCustomer = new Customer(name, email, phone, address);
        Account newAccount = new Account(newCustomer.getId(), paymentMethod);
        newAccount.save();
        newCustomer.save();
        Meter newMeter = new Meter(newAccount.getId(), fuelType, registerType, installationDate);
        MeterReading initialReading;
        if (newMeter.getRegisterType() == RegisterType.SINGLE_REGISTER) {
            initialReading = new MeterReading(newMeter.getId(), installationDate, BigDecimal.ZERO,
                    ReadingType.INITIAL);
        } else {
            initialReading = new MeterReading(newMeter.getId(), installationDate, BigDecimal.ZERO, BigDecimal.ZERO,
                    ReadingType.INITIAL);
        }
        initialReading.save();
        AccountTariff newAccountTariff = new AccountTariff(newAccount.getId(), selectedTariff.getId(), newMeter.getId(),
                newMeter.getInstallationDate(), null);

        newAccountTariff.save();
        newMeter.save();
        Utils.navigate("manageCustomers/manageCustomers.fxml", event, "Manage Customers");
    }

    public void onCancel(ActionEvent event) {
        Utils.navigate("manageCustomers/manageCustomers.fxml", event, "Manage Customers");
    }

    public void onBack(ActionEvent event) {
        // Navigate back to dashboard
        Utils.navigate("manageCustomers/manageCustomers.fxml", event, "Manage Customers");
    }

}
