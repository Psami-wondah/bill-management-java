package fx.enterMeterReading;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import backend.enums.ReadingType;
import backend.enums.RegisterType;
import backend.models.Account;
import backend.models.Customer;
import backend.models.Meter;
import backend.models.MeterReading;
import fx.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class EnterMeterReadingController {

    private Customer selectedCustomer;

    @FXML
    private Label label;

    @FXML
    private ComboBox<String> accountComboBox;

    @FXML
    private ComboBox<String> meterComboBox;

    @FXML
    private DatePicker readingDateField;

    @FXML
    private TextField readingField;

    @FXML
    private TextField dayReadingField;

    @FXML
    private TextField nightReadingField;

    public void initialize() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            return text.matches("\\d*(\\.\\d*)?") ? change : null;
        };

        readingField.setTextFormatter(new TextFormatter<>(filter));
        dayReadingField.setTextFormatter(new TextFormatter<>(filter));
        nightReadingField.setTextFormatter(new TextFormatter<>(filter));
        accountComboBox.setOnAction(event -> {
            String selectedAccountDisplay = accountComboBox.getSelectionModel().getSelectedItem();
            if (selectedAccountDisplay != null) {
                String accountId = selectedAccountDisplay.split(" - ")[0];
                meterComboBox.getItems().setAll(
                        Meter.objects.filter(m -> m.getAccountId().equals(accountId)).stream().map(Meter::toDisplay)
                                .toList());
            }
        });

        meterComboBox.onActionProperty().set(event -> {
            String selectedMeterString = meterComboBox.getValue();
            if (selectedMeterString == null) {
                return;
            }
            String meterId = selectedMeterString.split(" - ")[0];
            Meter selectedMeter = Meter.objects.findById(meterId).orElse(null);
            if (selectedMeter == null) {
                return;
            }

            if (selectedMeter.getRegisterType().equals(RegisterType.SINGLE_REGISTER)) {
                readingField.setDisable(false);
                dayReadingField.setDisable(true);
                nightReadingField.setDisable(true);
            } else if (selectedMeter.getRegisterType().equals(RegisterType.TWO_REGISTER)) {
                readingField.setDisable(true);
                dayReadingField.setDisable(false);
                nightReadingField.setDisable(false);
            }
        });
    }

    public void setSelectedCustomer(Customer customer) {
        this.selectedCustomer = customer;
        accountComboBox.getItems().addAll(
                Account.objects.filter(a -> a.getCustomerId().equals(selectedCustomer.getId())).stream()
                        .map(Account::toDisplay).toList());
        label.setText("Entering meter reading for: " + customer.getId());
    }

    @FXML
    public void onBack(ActionEvent event) {
        // Logic to go back to the previous screen
        label.getScene().getWindow().hide();

    }

    @FXML
    public void onCancel(ActionEvent event) {
        // Logic to cancel and go back to the previous screen
        onBack(event);
    }

    public void onSubmit(ActionEvent event) {
        List<String> errors = new ArrayList<>();

        String selectedMeterString = meterComboBox.getValue();
        String selectedAccountString = accountComboBox.getValue();
        LocalDate readingDate = readingDateField.getValue();
        String readingString = readingField.getText();
        String dayReadingString = dayReadingField.getText();
        String nightReadingString = nightReadingField.getText();

        if (selectedAccountString == null || selectedAccountString.isBlank()) {
            errors.add("Please select an account.");
        }
        if (selectedMeterString == null || selectedMeterString.isBlank()) {
            errors.add("Please select a meter.");
        }
        if (readingDate == null) {
            errors.add("Please select a reading date.");
        }
        if (readingField.isDisable()) {
            if (dayReadingString.isBlank()) {
                errors.add("Please enter a day reading.");
            }
            if (nightReadingString.isBlank()) {
                errors.add("Please enter a night reading.");
            }
        } else {
            if (readingString.isBlank()) {
                errors.add("Please enter a reading.");
            }
        }
        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }
        Account selectedAccount = Account.objects.findById(
                selectedAccountString.split(" - ")[0]).orElse(null);
        Meter selectedMeter = Meter.objects.findById(
                selectedMeterString.split(" - ")[0]).orElse(null);
        if (selectedAccount == null || selectedMeter == null) {
            errors.add("Selected account or meter not found.");
            Utils.showErrors(errors);
            return;
        }

        MeterReading existingReading = MeterReading.objects.findLatestReadingByMeterId(selectedMeter.getId());
        if (existingReading != null && !readingDate.isAfter(existingReading.getDate())) {
            errors.add("New reading date must be after the last reading date: "
                    + existingReading.getDate().toString());

        }
        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }

        if (selectedMeter.getRegisterType().equals(RegisterType.SINGLE_REGISTER)) {
            BigDecimal readingValue = new BigDecimal(readingString);
            MeterReading newReading = new MeterReading(
                    selectedMeter.getId(),
                    readingDate,
                    readingValue,
                    ReadingType.CUSTOMER);
            newReading.save();
        } else if (selectedMeter.getRegisterType().equals(RegisterType.TWO_REGISTER)) {
            BigDecimal dayReadingValue = new BigDecimal(dayReadingString);
            BigDecimal nightReadingValue = new BigDecimal(nightReadingString);
            MeterReading newReading = new MeterReading(
                    selectedMeter.getId(),
                    readingDate,
                    dayReadingValue,
                    nightReadingValue, ReadingType.CUSTOMER);
            newReading.save();
        }
        onBack(event);
    }

}
