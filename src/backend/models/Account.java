package backend.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import backend.enums.AccountStatus;
import backend.enums.PaymentMethod;
import backend.repositories.AccountRepository;

public class Account implements BaseModel {

    public static final AccountRepository objects = new AccountRepository();

    private final String id;
    private String customerId;
    private PaymentMethod paymentMethod;
    private LocalDate createdAt;
    private AccountStatus status;

    @JsonCreator
    public Account(@JsonProperty("id") String id,
            @JsonProperty("customerId") String customerId,
            @JsonProperty("paymentMethod") PaymentMethod paymentMethod,
            @JsonProperty("createdAt") LocalDate createdAt,
            @JsonProperty("status") AccountStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Account(String customerId, PaymentMethod paymentMethod) {
        this.id = objects.generateId();
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.createdAt = LocalDate.now();
        this.status = AccountStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void save() {
        objects.add(this);
    }

    public String toDisplay() {
        return id + " - " + paymentMethod.toString() + " - " + status.toString();
    }
}