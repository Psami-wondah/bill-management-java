package fx.manageTariffs;

import backend.models.Tariff;
import fx.Utils;
import fx.addTariff.AddTariffController;
import backend.enums.FuelType;
import backend.enums.RateType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageTariffsController {

    @FXML
    private Button addTariffButton;

    @FXML
    private TableView<Tariff> tariffTable;

    @FXML
    private TableColumn<Tariff, String> idColumn;

    @FXML
    private TableColumn<Tariff, String> nameColumn;

    @FXML
    private TableColumn<Tariff, FuelType> fuelColumn;

    @FXML
    private TableColumn<Tariff, RateType> rateColumn;

    @FXML
    private TableColumn<Tariff, BigDecimal> singleRateColumn;

    @FXML
    private TableColumn<Tariff, BigDecimal> dayRateColumn;

    @FXML
    private TableColumn<Tariff, BigDecimal> nightRateColumn;

    @FXML
    private TableColumn<Tariff, BigDecimal> standingChargeColumn;

    @FXML
    private TableColumn<Tariff, BigDecimal> vatColumn;

    @FXML
    private TableColumn<Tariff, LocalDate> effectiveFromColumn;

    @FXML
    private TableColumn<Tariff, LocalDate> effectiveToColumn;

    public List<Tariff> loadTariffs() {
        return Tariff.objects.findAll();
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        fuelColumn.setCellValueFactory(new PropertyValueFactory<>("fuelType"));
        rateColumn.setCellValueFactory(new PropertyValueFactory<>("rateType"));
        singleRateColumn.setCellValueFactory(new PropertyValueFactory<>("singleRate"));
        dayRateColumn.setCellValueFactory(new PropertyValueFactory<>("dayRate"));
        nightRateColumn.setCellValueFactory(new PropertyValueFactory<>("nightRate"));
        standingChargeColumn.setCellValueFactory(new PropertyValueFactory<>("dailyStandingCharge"));
        vatColumn.setCellValueFactory(new PropertyValueFactory<>("vatRate"));
        effectiveFromColumn.setCellValueFactory(new PropertyValueFactory<>("effectiveFrom"));
        effectiveToColumn.setCellValueFactory(new PropertyValueFactory<>("effectiveTo"));

        setupFormatting();
        List<Tariff> tariffs = loadTariffs();
        tariffTable.setItems(FXCollections.observableArrayList(tariffs));

        tariffTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Tariff selected = tariffTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openTariff(selected);
                }
            }
        });
    }

    private void openTariff(Tariff t) {
        // TODO: navigate to "Edit Tariff" screen or show a dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../addTariff/addTariff.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AddTariffController controller = loader.getController();
        controller.setEditMode(true);
        controller.setTariffInView(t);
        controller.onEditSave(updatedTariff -> {
            tariffTable.refresh();
        });

        Stage dialog = new Stage();
        dialog.setTitle("Edit Tariff");
        dialog.initOwner(tariffTable.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    @FXML
    private void onAddTariff(ActionEvent event) {
        Utils.navigate("addTariff/addTariff.fxml", event, "Add Tariff");
    }

    private void setupFormatting() {
        Utils.setupFormatter(singleRateColumn, v -> "£" + v);
        Utils.setupFormatter(dayRateColumn, v -> "£" + v);
        Utils.setupFormatter(nightRateColumn, v -> "£" + v);
        Utils.setupFormatter(standingChargeColumn, v -> "£" + v);
        Utils.setupFormatter(vatColumn, v -> v.multiply(BigDecimal.valueOf(100)) + "%");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Utils.setupFormatter(effectiveFromColumn, d -> d.format(fmt));
        Utils.setupFormatter(effectiveToColumn, d -> d.format(fmt));
    }

    public void onBack(ActionEvent event) {
        // Navigate back to dashboard
        Utils.navigate("dashboard/dashboard.fxml", event, "Dashboard");
    }
}
