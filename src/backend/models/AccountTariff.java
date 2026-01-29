package backend.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import backend.repositories.AccountTariffRepository;

public class AccountTariff implements BaseModel {

    public static final AccountTariffRepository objects = new AccountTariffRepository();

    private final String id;
    private String accountId;
    private String tariffId;
    private String meterId;
    private LocalDate startDate;
    private LocalDate endDate;

    @JsonCreator
    public AccountTariff(@JsonProperty("id") String id,
            @JsonProperty("accountId") String accountId,
            @JsonProperty("tariffId") String tariffId,
            @JsonProperty("meterId") String meterId,
            @JsonProperty("startDate") LocalDate startDate,
            @JsonProperty("endDate") LocalDate endDate) {
        this.id = id;
        this.accountId = accountId;
        this.tariffId = tariffId;
        this.meterId = meterId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AccountTariff(String accountId, String tariffId, String meterId, LocalDate startDate, LocalDate endDate) {
        this.id = objects.generateId();
        this.accountId = accountId;
        this.tariffId = tariffId;
        this.meterId = meterId;
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

    public String getMeterId() {
        return meterId;
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