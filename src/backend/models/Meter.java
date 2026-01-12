package backend.models;

import java.time.LocalDate;

import backend.enums.FuelType;
import backend.enums.RegisterType;
import backend.repositories.MeterRepository;

public class Meter implements BaseModel {

    public static final MeterRepository objects = new MeterRepository();

    private final String id;
    private String accountId;
    private FuelType fuelType;
    private RegisterType registerType;
    private LocalDate installationDate;

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

}
