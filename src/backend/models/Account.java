package backend.models;

import backend.enums.AccountStatus;
import backend.repositories.AccountRepository;

public class Account implements BaseModel {

    public static final AccountRepository objects = new AccountRepository();

    private final String id;
    private String customerId;
    private String paymentMethod;
    private String createdAt;
    private AccountStatus status;

    public Account(String customerId, String paymentMethod, String createdAt) {
        this.id = objects.generateId();
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.status = AccountStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
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
}