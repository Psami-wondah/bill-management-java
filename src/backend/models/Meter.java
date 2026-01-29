package backend.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import backend.enums.FuelType;
import backend.enums.RegisterType;
import backend.repositories.MeterRepository;

public class Meter implements BaseModel {

    @JsonIgnore
    public static final MeterRepository objects = new MeterRepository();

    private final String id;
    private String accountId;
    private FuelType fuelType;
    private RegisterType registerType;
    private LocalDate installationDate;

    @JsonCreator
    public Meter(@JsonProperty("id") String id, @JsonProperty("accountId") String accountId,
            @JsonProperty("fuelType") FuelType fuelType, @JsonProperty("registerType") RegisterType registerType,
            @JsonProperty("installationDate") LocalDate installationDate) {
        this.id = id;
        this.accountId = accountId;
        this.fuelType = fuelType;
        this.registerType = registerType;
        this.installationDate = installationDate;
    }

    public Meter(String accountId, FuelType fuelType, RegisterType registerType, LocalDate installationDate) {
        this.id = objects.generateId();
        this.accountId = accountId;
        this.fuelType = fuelType;
        this.registerType = registerType;
        this.installationDate = installationDate;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public RegisterType getRegisterType() {
        return registerType;
    }

    public void save() {
        objects.add(this);
    }

    public String toDisplay() {
        return id + " - " + fuelType + " - " + registerType;
    }

}
