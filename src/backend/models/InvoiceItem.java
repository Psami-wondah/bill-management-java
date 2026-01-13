package backend.models;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvoiceItem implements Serializable {

    private String description;
    private BigDecimal quantity;
    private BigDecimal amount;
    private BigDecimal unitPrice;

    @JsonCreator
    public InvoiceItem(@JsonProperty("description") String description,
            @JsonProperty("quantity") BigDecimal quantity,
            @JsonProperty("unitPrice") BigDecimal unitPrice,
            @JsonProperty("amount") BigDecimal amount) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }

    public InvoiceItem(String description, BigDecimal quantity, BigDecimal unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = unitPrice.multiply(quantity);
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

}
