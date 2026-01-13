package backend.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import backend.enums.PaymentMethod;
import backend.repositories.PaymentRepository;

public class Payment implements BaseModel {

    public static final PaymentRepository objects = new PaymentRepository();

    private final String id;
    private String accountId;
    private String invoiceId;
    private BigDecimal amount;
    private String paymentDate;
    private PaymentMethod paymentMethod;

    @JsonCreator
    public Payment(@JsonProperty("id") String id, @JsonProperty("invoiceId") String invoiceId,
            @JsonProperty("accountId") String accountId, @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("paymentDate") String paymentDate,
            @JsonProperty("paymentMethod") PaymentMethod paymentMethod) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.accountId = accountId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    public Payment(String invoiceId, String accountId, BigDecimal amount, String paymentDate,
            PaymentMethod paymentMethod) {
        this.id = objects.generateId();
        this.invoiceId = invoiceId;
        this.accountId = accountId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void save() {
        objects.add(this);
    }

}
