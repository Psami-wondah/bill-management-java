package fx.viewInvoice;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.math.RoundingMode;

import backend.models.Customer;
import backend.models.Invoice;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import io.github.cdimascio.dotenv.Dotenv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ViewInvoiceController {
    private Customer selectedCustomer;

    private Invoice invoice;

    @FXML
    private Label invoiceIdLabel;

    @FXML
    private Label accountIdLabel;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Label subTotalLabel;

    @FXML
    private Label vatLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label periodStartLabel;

    @FXML
    private Label periodEndLabel;

    @FXML
    private VBox invoiceItemsBox;

    public void setSelectedCustomer(Customer customer) {
        this.selectedCustomer = customer;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        invoiceIdLabel.setText("Invoice ID: " + invoice.getId());
        accountIdLabel.setText("Account ID: " + invoice.getAccountId());
        totalAmountLabel.setText("Total Amount £: " + invoice.getTotal());
        subTotalLabel.setText("Sub Total £: " + invoice.getSubTotal().setScale(2, RoundingMode.HALF_UP));
        vatLabel.setText("VAT £: " + invoice.getVat());
        statusLabel.setText("Status: " + invoice.getStatus().name());
        periodStartLabel.setText("Period Start: " + invoice.getPeriodStart());
        periodEndLabel.setText("Period End: " + invoice.getPeriodEnd());

        // Populate invoice items
        invoiceItemsBox.getChildren().clear();

        invoice.getItems().forEach(item -> {
            Label itemLabel = new Label("- " + item.toDisplay());
            invoiceItemsBox.getChildren().add(itemLabel);
        });
    }

    @FXML
    public void sendEmail() {
        // Email code was gotten from the internet
        Dotenv dotenv = Dotenv.load();

        try {
            String apiKey = dotenv.get("RESEND_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                System.out.println("RESEND_API_KEY not set");
                return;
            }

            StringBuilder body = new StringBuilder();

            body.append("INVOICE\n");
            body.append("Invoice ID: ").append(invoice.getId()).append("\n");
            body.append("Account ID: ").append(invoice.getAccountId()).append("\n");
            body.append("Period: ")
                    .append(invoice.getPeriodStart())
                    .append(" to ")
                    .append(invoice.getPeriodEnd())
                    .append("\n\n");

            body.append("ITEMS\n");

            int index = 1;
            for (var item : invoice.getItems()) {
                body.append(index++).append(". ")
                        .append(item.getDescription())
                        .append(" | Qty: ").append(item.getQuantity())
                        .append(" | Unit: ").append(item.getUnitPrice())
                        .append(" | Amount: ").append(item.getAmount().setScale(2, RoundingMode.HALF_UP))
                        .append("\n");
            }

            body.append("\n");
            body.append("Subtotal: ").append(invoice.getSubTotal().setScale(2, RoundingMode.HALF_UP)).append("\n");
            body.append("VAT: ").append(invoice.getVat()).append("\n");
            body.append("TOTAL: ").append(invoice.getTotal()).append("\n");
            body.append("\nStatus: ").append(invoice.getStatus());

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode emailPayload = mapper.createObjectNode();

            emailPayload.put("from", "Billing <billing@tempmail.psami.com>");
            emailPayload.putArray("to").add(selectedCustomer.getEmail());
            emailPayload.put("subject", "Invoice " + invoice.getId());
            emailPayload.put("text", body.toString());
            emailPayload.put("reply_to", "billing@tempmail.psami.com");

            String json = mapper.writeValueAsString(emailPayload);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(res -> {
                        System.out.println("Email sent. Status: " + res.statusCode());

                    })
                    .exceptionally(e -> {
                        System.err.println("Email failed: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onExit() {
        invoice = null;
        this.invoiceIdLabel.getScene().getWindow().hide();
    }
}
