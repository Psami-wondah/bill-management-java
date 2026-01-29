package fx.addTariff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import backend.enums.FuelType;
import backend.enums.RateType;
import backend.models.Tariff;
import fx.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class AddTariffController {

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> fuelTypeComboBox;

    @FXML
    private ComboBox<String> rateTypeComboBox;

    @FXML
    private TextField singleRateField;

    @FXML
    private TextField dayRateField;

    @FXML
    private TextField nightRateField;

    @FXML
    private TextField dailyStandingChargeField;

    @FXML
    private TextField vatRateField;

    @FXML
    private DatePicker effectiveFromField;

    @FXML
    private DatePicker effectiveToField;

    private Tariff tariffInView;

    private boolean editMode = false;

    private Consumer<Tariff> onSaved;

    public void initialize() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            return text.matches("\\d*(\\.\\d*)?") ? change : null;
        };

        singleRateField.setTextFormatter(new TextFormatter<>(filter));
        dayRateField.setTextFormatter(new TextFormatter<>(filter));
        nightRateField.setTextFormatter(new TextFormatter<>(filter));
        dailyStandingChargeField.setTextFormatter(new TextFormatter<>(filter));
        vatRateField.setTextFormatter(new TextFormatter<>(filter));

        fuelTypeComboBox.getItems().addAll(
                FuelType.GAS.name(),
                FuelType.ELECTRICITY.name());

        rateTypeComboBox.getItems().addAll(
                RateType.SINGLE_RATE.name(),
                RateType.TWO_RATE.name());

        fuelTypeComboBox.onActionProperty().set(event -> {
            String selectedFuelType = fuelTypeComboBox.getValue();

            if (selectedFuelType.equals(FuelType.GAS.name())) {
                rateTypeComboBox.getItems().setAll(
                        RateType.SINGLE_RATE.name());
            } else if (selectedFuelType.equals(FuelType.ELECTRICITY.name())) {
                rateTypeComboBox.getItems().setAll(
                        RateType.SINGLE_RATE.name(),
                        RateType.TWO_RATE.name());
            }
        });

        rateTypeComboBox.onActionProperty().set(event -> {
            String selectedRateType = rateTypeComboBox.getValue();
            if (selectedRateType.equals(RateType.SINGLE_RATE.name())) {
                singleRateField.setDisable(false);
                dayRateField.setDisable(true);
                nightRateField.setDisable(true);
            } else if (selectedRateType.equals(RateType.TWO_RATE.name())) {
                singleRateField.setDisable(true);
                dayRateField.setDisable(false);
                nightRateField.setDisable(false);
            }
        });

    }

    public void onSubmit(ActionEvent event) {
        List<String> errors = new ArrayList<>();
        String name = nameField.getText();
        String fuelType = fuelTypeComboBox.getValue();
        String rateType = rateTypeComboBox.getValue();
        String singleRateString = singleRateField.getText();
        String dayRateString = dayRateField.getText();
        String nightRateString = nightRateField.getText();
        String dailyStandingChargeString = dailyStandingChargeField.getText();
        String vatRateString = vatRateField.getText();
        LocalDate effectiveFrom = effectiveFromField.getValue();
        LocalDate effectiveTo = effectiveToField.getValue();
        // Validate required fields

        if (name == null || name.trim().isEmpty())
            errors.add("Name is required");

        if (fuelType == null || fuelType.trim().isEmpty())
            errors.add("Fuel Type is required");

        if (rateType == null || rateType.trim().isEmpty())
            errors.add("Rate Type is required");

        if (rateType != null) {
            if (rateType.equals(RateType.SINGLE_RATE.name())) {
                if (singleRateString == null || singleRateString.trim().isEmpty())
                    errors.add("Single Rate is required for Single Rate tariffs");
            } else if (rateType.equals(RateType.TWO_RATE.name())) {
                if (dayRateString == null || dayRateString.trim().isEmpty())
                    errors.add("Day Rate is required for Two Rate tariffs");
                if (nightRateString == null || nightRateString.trim().isEmpty())
                    errors.add("Night Rate is required for Two Rate tariffs");
            }
        }

        if (dailyStandingChargeString == null || dailyStandingChargeString.trim().isEmpty())
            errors.add("Daily Standing Charge is required");

        if (vatRateString == null || vatRateString.trim().isEmpty())
            errors.add("VAT Rate is required");

        if (effectiveFrom == null)
            errors.add("Effective From date is required");

        if (effectiveTo == null)
            errors.add("Effective To date is required");

        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }

        if (editMode && tariffInView != null) {
            // Update existing tariff
            tariffInView.setName(name);
            tariffInView.setFuelType(FuelType.valueOf(fuelType));
            tariffInView.setRateType(RateType.valueOf(rateType));
            if (rateType.equals(RateType.SINGLE_RATE.name())) {
                BigDecimal singleRate = singleRateString.isEmpty() ? BigDecimal.ZERO : new BigDecimal(singleRateString);
                tariffInView.setSingleRate(singleRate);
            } else {
                BigDecimal dayRate = dayRateString.isEmpty() ? BigDecimal.ZERO : new BigDecimal(dayRateString);
                BigDecimal nightRate = nightRateString.isEmpty() ? BigDecimal.ZERO : new BigDecimal(nightRateString);
                tariffInView.setDayRate(dayRate);
                tariffInView.setNightRate(nightRate);
            }
            BigDecimal dailyStandingCharge = new BigDecimal(dailyStandingChargeString);
            BigDecimal vatRate = new BigDecimal(vatRateString).divide(BigDecimal.valueOf(100), 10,
                    RoundingMode.HALF_UP);
            tariffInView.setDailyStandingCharge(dailyStandingCharge);
            tariffInView.setVatRate(vatRate);
            tariffInView.setEffectiveFrom(effectiveFrom);
            tariffInView.setEffectiveTo(effectiveTo);

            tariffInView.save();
            if (onSaved != null) {
                onSaved.accept(tariffInView);
            }
            // Close the edit window
            Stage stage = (Stage) nameField.getScene().getWindow();

            stage.close();
            return;
        }

        Tariff newTariff;
        BigDecimal singleRate = singleRateString.isEmpty() ? BigDecimal.ZERO : new BigDecimal(singleRateString);
        BigDecimal dayRate = dayRateString.isEmpty() ? BigDecimal.ZERO : new BigDecimal(dayRateString);
        BigDecimal nightRate = nightRateString.isEmpty() ? BigDecimal.ZERO : new BigDecimal(nightRateString);
        BigDecimal dailyStandingCharge = new BigDecimal(dailyStandingChargeString);
        BigDecimal vatRate = new BigDecimal(vatRateString).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        if (rateType.equals(RateType.SINGLE_RATE.name())) {
            newTariff = new Tariff(name, FuelType.valueOf(fuelType), RateType.SINGLE_RATE, singleRate,
                    dailyStandingCharge, vatRate,
                    effectiveFrom, effectiveTo);
        } else {
            newTariff = new Tariff(name, FuelType.valueOf(fuelType), RateType.TWO_RATE, dayRate, nightRate,
                    dailyStandingCharge, vatRate,
                    effectiveFrom, effectiveTo);
        }
        newTariff.save();

        Utils.navigate("manageTariffs/manageTariffs.fxml", event, "Manage Tariffs");

    }

    public void onCancel(ActionEvent event) {
        if (editMode) {
            nameField.getScene().getWindow().hide();
            return;
        }
        Utils.navigate("manageTariffs/manageTariffs.fxml", event, "Manage Tariffs");
    }

    public void onBack(ActionEvent event) {
        if (editMode) {
            nameField.getScene().getWindow().hide();
            return;
        }
        Utils.navigate("manageTariffs/manageTariffs.fxml", event, "Manage Tariffs");
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if (editMode) {
            fuelTypeComboBox.setDisable(true); // Disable fuel type selection in edit mode
            rateTypeComboBox.setDisable(true); // Disable rate type selection in edit mode
        }
    }

    public void setTariffInView(Tariff tariff) {
        this.tariffInView = tariff;

        if (tariffInView != null) {
            // Populate fields with existing tariff data for editing
            nameField.setText(tariffInView.getName());
            fuelTypeComboBox.setValue(tariffInView.getFuelType().name());
            rateTypeComboBox.setValue(tariffInView.getRateType().name());
            if (tariffInView.getRateType() == RateType.SINGLE_RATE) {
                singleRateField.setText(tariffInView.getSingleRate().toString());
            } else if (tariffInView.getRateType() == RateType.TWO_RATE) {
                dayRateField.setText(tariffInView.getDayRate().toString());
                nightRateField.setText(tariffInView.getNightRate().toString());
            }
            dailyStandingChargeField.setText(tariffInView.getDailyStandingCharge().toString());
            vatRateField.setText(tariffInView.getVatRate().multiply(BigDecimal.valueOf(100)).toString());
            effectiveFromField.setValue(tariffInView.getEffectiveFrom());
            effectiveToField.setValue(tariffInView.getEffectiveTo());

            if (tariffInView.getRateType().equals(RateType.SINGLE_RATE)) {
                singleRateField.setDisable(false);
                dayRateField.setDisable(true);
                nightRateField.setDisable(true);
            } else if (tariffInView.getRateType().equals(RateType.TWO_RATE)) {
                singleRateField.setDisable(true);
                dayRateField.setDisable(false);
                nightRateField.setDisable(false);
            }
        }

    }

    public void onEditSave(Consumer<Tariff> onSavedCallback) {
        this.onSaved = onSavedCallback;
    }

}
