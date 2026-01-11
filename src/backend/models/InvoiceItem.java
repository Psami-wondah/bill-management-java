package backend.models;

import java.io.Serializable;
import java.math.BigDecimal;

public class InvoiceItem implements Serializable {

    private String description;
    private BigDecimal quantity;
    private BigDecimal amount;
    private BigDecimal unitPrice;

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
