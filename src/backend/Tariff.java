package backend;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.UUID;

public class Tariff implements Serializable {

    public static final TariffRepository OBJECTS = new TariffRepository();

    public static final double GAS_CORRECTION_FACTOR = 1.02264;
    public static final double GAS_CALORIFIC_VALUE = 39.4; // MJ/m3
    public static final double FT3_TO_M3 = 2.83;

    private final String id;
    private String userId;
    private String name;
    private String meterType;
    private double rate;
    private double dailyStandingCharge;
    private double vat;

    public Tariff(String userId, String name, String meterType, double rate, double dailyStandingCharge, double vat) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.name = name;
        this.rate = rate;
        this.meterType = meterType;
        this.dailyStandingCharge = dailyStandingCharge;
        this.vat = vat;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getDailyStandingCharge() {
        return dailyStandingCharge;
    }

    public void setDailyStandingCharge(double dailyStandingCharge) {
        this.dailyStandingCharge = dailyStandingCharge;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public String getId() {
        return id;
    }

    public double calculateGasConsumption(double openingReading, double closingReading) {
        double reading = closingReading - openingReading;
        // Convert to m3
        double volumeM3 = reading * FT3_TO_M3;
        // Apply correction factor
        double correctedVolumeM3 = volumeM3 * GAS_CORRECTION_FACTOR;
        // Convert to kWh
        double consumptionKWh = (correctedVolumeM3 * GAS_CALORIFIC_VALUE) / 3.6;
        return consumptionKWh;
    }

    public double calculateElectricityConsumption(double openingReading, double closingReading) {
        return closingReading - openingReading;
    }

    public double calculateBill(int days, double openingReading, double closingReading) {
        DecimalFormat df = new DecimalFormat("0.00");
        double consumption = this.meterType.equals("Gas")
                ? calculateGasConsumption(openingReading, closingReading)
                : calculateElectricityConsumption(openingReading, closingReading);

        // Charges in pence
        double standingChargePence = dailyStandingCharge * days;
        double consumptionChargePence = rate * consumption;
        double subtotalPence = standingChargePence + consumptionChargePence;
        double vatPence = subtotalPence * (vat / 100);
        double totalPence = subtotalPence + vatPence;

        // Convert to pounds
        double standingChargePounds = standingChargePence / 100.0;
        double consumptionChargePounds = consumptionChargePence / 100.0;

        double vatPounds = vatPence / 100.0;
        double totalPounds = totalPence / 100.0;

        // PRINT CALCULATION BREAKDOWN
        System.out.println("---- BILL BREAKDOWN ----");
        System.out.println(
                "Standing Charge: " + df.format(standingChargePence) + "p (" + df.format(standingChargePounds) + "£)");
        System.out.println("Consumption Charge: " + df.format(consumptionChargePence) + "p ("
                + df.format(consumptionChargePounds) + "£)");
        System.out.println("VAT: " + df.format(vatPence) + "p (" + df.format(vatPounds) + "£)");
        System.out.println("-------------------------");
        System.out.println("TOTAL: " + df.format(totalPence) + "p (" + df.format(totalPounds) + "£)");
        System.out.println("-------------------------");

        return totalPounds; // still return value in pounds
    }

    public void save() {
        OBJECTS.add(this);
    }

}
