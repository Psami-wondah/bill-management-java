package backend.models;

import java.time.LocalDate;

import backend.repositories.AccountTarrifRepository;

public class AccountTarrif implements BaseModel {

    public static final AccountTarrifRepository objects = new AccountTarrifRepository();

    private final String id;
    private String accountId;
    private String tariffId;
    private LocalDate startDate;
    private LocalDate endDate;

    public AccountTarrif(String accountId, String tariffId, LocalDate startDate, LocalDate endDate) {
        this.id = objects.generateId();
        this.accountId = accountId;
        this.tariffId = tariffId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getTariffId() {
        return tariffId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void save() {
        objects.add(this);
    }
}