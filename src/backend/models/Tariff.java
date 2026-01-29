package backend.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import backend.enums.FuelType;
import backend.enums.RateType;
import backend.repositories.TariffRepository;

public class Tariff implements BaseModel {

    public static final TariffRepository objects = new TariffRepository();

    public static final BigDecimal GAS_CORRECTION_FACTOR = new BigDecimal("1.02264");
    public static final BigDecimal GAS_CALORIFIC_VALUE = new BigDecimal("39.4"); // MJ/m3
    public static final BigDecimal FT3_TO_M3 = new BigDecimal("2.83");

    private final String id;
    private String name;
    private FuelType fuelType;
    private RateType rateType;
    private BigDecimal singleRate;
    private BigDecimal dayRate;
    private BigDecimal nightRate;
    private BigDecimal dailyStandingCharge;
    private BigDecimal vatRate;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    // Constructor for JSON deserialization
    @JsonCreator
    public Tariff(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("fuelType") FuelType fuelType,
            @JsonProperty("rateType") RateType rateType,
            @JsonProperty("singleRate") BigDecimal singleRate,
            @JsonProperty("dayRate") BigDecimal dayRate,
            @JsonProperty("nightRate") BigDecimal nightRate,
            @JsonProperty("dailyStandingCharge") BigDecimal dailyStandingCharge,
            @JsonProperty("vatRate") BigDecimal vatRate,
            @JsonProperty("effectiveFrom") LocalDate effectiveFrom,
            @JsonProperty("effectiveTo") LocalDate effectiveTo) {

        this.id = id;
        this.name = name;
        this.fuelType = fuelType;
        this.rateType = rateType;
        this.singleRate = singleRate;
        this.dayRate = dayRate;
        this.nightRate = nightRate;
        this.dailyStandingCharge = dailyStandingCharge;
        this.vatRate = vatRate;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public Tariff(String name, FuelType fuelType, RateType rateType, BigDecimal singleRate,
            BigDecimal dailyStandingCharge,
            BigDecimal vatRate, LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.id = objects.generateId();
        this.name = name;
        this.fuelType = fuelType;
        this.rateType = rateType;
        this.singleRate = singleRate;
        this.dailyStandingCharge = dailyStandingCharge;
        this.vatRate = vatRate;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public Tariff(String name, FuelType fuelType, RateType rateType, BigDecimal dayRate, BigDecimal nightRate,
            BigDecimal dailyStandingCharge, BigDecimal vatRate, LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.id = objects.generateId();
        this.name = name;
        this.fuelType = fuelType;
        this.rateType = rateType;
        this.dayRate = dayRate;
        this.nightRate = nightRate;
        this.dailyStandingCharge = dailyStandingCharge;
        this.vatRate = vatRate;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getSingleRate() {
        return singleRate;
    }

    public BigDecimal getDayRate() {
        return dayRate;
    }

    public void setDayRate(BigDecimal dayRate) {
        this.dayRate = dayRate;
    }

    public BigDecimal getNightRate() {
        return nightRate;
    }

    public void setNightRate(BigDecimal nightRate) {
        this.nightRate = nightRate;
    }

    public void setSingleRate(BigDecimal singleRate) {
        this.singleRate = singleRate;
    }

    public BigDecimal getDailyStandingCharge() {
        return dailyStandingCharge;
    }

    public void setDailyStandingCharge(BigDecimal dailyStandingCharge) {
        this.dailyStandingCharge = dailyStandingCharge;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    private BigDecimal calculateCost(BigDecimal usage, BigDecimal rate) {
        System.out.println("Calculating cost for usage: " + usage + " at rate: " + rate);
        if (this.fuelType == FuelType.GAS) {
            BigDecimal usageInM3 = usage.multiply(FT3_TO_M3);
            BigDecimal usageInKwh = usageInM3
                    .multiply(GAS_CORRECTION_FACTOR)
                    .multiply(GAS_CALORIFIC_VALUE)
                    .divide(new BigDecimal("3.6"), 10, RoundingMode.HALF_UP);

            System.out.println("Usage in ft3: " + usage);
            System.out.println("Usage in m3: " + usageInM3);
            System.out.println("Usage in kWh: " + usageInKwh);
            return usageInKwh.multiply(rate);
        } else {
            return usage.multiply(rate);
        }

    }

    public BigDecimal calculateUsageCost(BigDecimal usage) {
        return calculateCost(usage, singleRate);
    }

    public BigDecimal calculateDayUsageCost(BigDecimal dayUsage) {
        return calculateCost(dayUsage, dayRate);
    }

    public BigDecimal calculateNightUsageCost(BigDecimal nightUsage) {
        return calculateCost(nightUsage, nightRate);
    }

    public void save() {
        objects.add(this);
    }

    public String toDisplay() {
        return id + " " + name + " (" + fuelType + ", " + rateType + ")";
    }

}
