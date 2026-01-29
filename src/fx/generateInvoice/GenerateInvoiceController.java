package fx.generateInvoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backend.enums.RateType;
import backend.enums.UsageKeys;
import backend.models.Account;
import backend.models.AccountTariff;
import backend.models.Customer;
import backend.models.Invoice;
import backend.models.InvoiceItem;
import backend.models.Meter;
import backend.models.MeterReading;
import backend.models.Tariff;
import fx.Utils;
import fx.manageInvoices.ManageInvoicesController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class GenerateInvoiceController {

    private Customer selectedCustomer;

    @FXML
    private Label label;

    @FXML
    private ComboBox<String> accountComboBox;

    @FXML
    private ComboBox<String> meterComboBox;

    @FXML
    private DatePicker startDateField;

    @FXML
    private DatePicker endDateField;

    @FXML
    public void initialize() {
        accountComboBox.setOnAction(event -> {
            String selectedAccountDisplay = accountComboBox.getSelectionModel().getSelectedItem();
            if (selectedAccountDisplay != null) {
                String accountId = selectedAccountDisplay.split(" - ")[0];
                meterComboBox.getItems().setAll(
                        Meter.objects.filter(m -> m.getAccountId().equals(accountId)).stream().map(Meter::toDisplay)
                                .toList());
            }
        });
    }

    @FXML
    public void onBack(ActionEvent event) {
        // Logic to go back to the previous screen
        Utils.navigate("manageInvoices/manageInvoices.fxml", event, "Manage Invoices", loader -> {
            ManageInvoicesController controller = loader.getController();
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
        label.setText("Generating invoice for: " + customer.getId());
    }

    public void onSubmit(ActionEvent event) {
        List<String> errors = new ArrayList<>();

        String selectedMeterString = meterComboBox.getValue();
        String selectedAccountString = accountComboBox.getValue();
        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();

        if (selectedAccountString == null || selectedAccountString.isBlank()) {
            errors.add("Please select an account.");
        }
        if (selectedMeterString == null || selectedMeterString.isBlank()) {
            errors.add("Please select a meter.");
        }
        if (startDate == null) {
            errors.add("Please select a start date.");
        }
        if (endDate == null) {
            errors.add("Please select an end date.");
        }
        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }
        Meter selectedMeter = Meter.objects.findById(
                selectedMeterString.split(" - ")[0]).orElse(null);

        if (selectedMeter == null) {
            errors.add("Selected meter not found.");
        }
        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }

        AccountTariff accountTariff = AccountTariff.objects
                .filter(at -> at.getMeterId().equals(selectedMeter.getId())
                        && (at.getStartDate().isBefore(startDate) || at.getStartDate().isEqual(startDate))
                        && (at.getEndDate() == null || at.getEndDate().isAfter(startDate)))
                .stream()
                .findFirst()
                .orElse(null);

        if (accountTariff == null) {
            errors.add("No active tariff found for this meter during the specified period.");
        }

        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }
        Tariff tariff = Tariff.objects.findById(accountTariff.getTariffId()).orElse(null);

        if (tariff == null) {
            errors.add("Tariff details not found. Cannot generate invoice.");
        }

        if (!MeterReading.objects.checkValidUsagePeriod(selectedMeter.getId(), startDate, endDate)) {
            errors.add("Billing period is not fully covered by meter readings");
        }
        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }
        Map<UsageKeys, BigDecimal> usage = MeterReading.objects
                .getUsageForPeriodByMeter(selectedMeter, startDate, endDate);
        if (usage == null) {
            errors.add("Error calculating usage for the specified period.");
        }
        if (!errors.isEmpty()) {
            Utils.showErrors(errors);
            return;
        }

        List<InvoiceItem> invoiceItems = new ArrayList<>();

        long billingDays = ChronoUnit.DAYS.between(startDate, endDate);

        invoiceItems
                .add(new InvoiceItem("Standing Charge", BigDecimal.valueOf(billingDays),
                        tariff.getDailyStandingCharge()));

        if (tariff.getRateType() == RateType.SINGLE_RATE) {
            BigDecimal usageAmount = usage.get(UsageKeys.SINGLE);
            BigDecimal cost = tariff.calculateUsageCost(usageAmount);
            invoiceItems.add(new InvoiceItem("Usage Charge", BigDecimal.valueOf(billingDays),
                    cost.divide(BigDecimal.valueOf(billingDays), 2, RoundingMode.HALF_UP),
                    cost));
        } else {
            BigDecimal dayUsage = usage.get(UsageKeys.DAY);
            BigDecimal nightUsage = usage.get(UsageKeys.NIGHT);
            BigDecimal dayCost = tariff.calculateDayUsageCost(dayUsage);
            BigDecimal nightCost = tariff.calculateNightUsageCost(nightUsage);
            invoiceItems.add(new InvoiceItem("Day Usage Charge", BigDecimal.valueOf(billingDays),
                    dayCost.divide(BigDecimal.valueOf(billingDays), 2, RoundingMode.HALF_UP),
                    dayCost));
            invoiceItems.add(new InvoiceItem("Night Usage Charge", BigDecimal.valueOf(billingDays),
                    nightCost.divide(BigDecimal.valueOf(billingDays), 2, RoundingMode.HALF_UP),
                    nightCost));
        }

        BigDecimal subTotal = invoiceItems.stream()
                .map(InvoiceItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal vat = subTotal.multiply(tariff.getVatRate()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subTotal.add(vat).setScale(2, RoundingMode.HALF_UP);

        Invoice newInvoice = new Invoice(accountTariff.getAccountId(), accountTariff.getId(), subTotal, vat, total,
                backend.enums.InvoiceStatus.ISSUED, startDate, endDate, invoiceItems);

        newInvoice.save();

        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Invoice generated successfully with ID: " + newInvoice.getId());
        infoMessages.add("Invoice Summary:");
        infoMessages.add("Days: " + billingDays);
        infoMessages.add("Subtotal: £" + subTotal.setScale(2, RoundingMode.HALF_UP));
        infoMessages.add("VAT: £" + vat);
        infoMessages.add("Total: £" + total);
        Utils.showInfo("Invoice Generated", infoMessages);

        Utils.navigate("manageInvoices/manageInvoices.fxml", event, "Manage Invoices", loader -> {
            ManageInvoicesController controller = loader.getController();
            controller.setSelectedCustomer(selectedCustomer);
            controller.setActiveAccount(accountTariff.getAccountId());
        });
    }

}
