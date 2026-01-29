package fx;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

public class Utils {
    public static void navigate(String fxmlFile, Event event, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlFile));
            Parent page = loader.load();

            Scene scene = new Scene(page);

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.setTitle(title);

            // Let the stage resize to fit the new scene
            stage.sizeToScene();

            // Now center the window on the screen
            stage.centerOnScreen();

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void navigate(String fxmlFile, Event event, String title,
            Consumer<FXMLLoader> onLoad) {
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlFile));
            Parent page = loader.load();

            if (onLoad != null) {
                onLoad.accept(loader);
            }

            Scene scene = new Scene(page);

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.setTitle(title);

            // Let the stage resize to fit the new scene
            stage.sizeToScene();

            // Now center the window on the screen
            stage.centerOnScreen();

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <F, T> void setupFormatter(TableColumn<F, T> column, Function<T, String> formatter) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatter.apply(item));
            }
        });
    }

    public static void showErrors(List<String> errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please fix the following:");
        alert.setContentText(String.join("\n", errors));
        alert.showAndWait();
    }

    public static void showInfo(String title, List<String> messages) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(String.join("\n", messages));
        alert.showAndWait();
    }

}