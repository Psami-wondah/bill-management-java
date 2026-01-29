package test.backend.models;

import backend.enums.FuelType;
import backend.enums.RateType;
import backend.models.Tariff;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TariffTest {

        @Test
        void electricity_singleRate_costIsUsageTimesRate() {
                Tariff tariff = new Tariff(
                                "Elec Standard",
                                FuelType.ELECTRICITY,
                                RateType.SINGLE_RATE,
                                new BigDecimal("0.30"),
                                new BigDecimal("0.50"),
                                new BigDecimal("0.20"),
                                LocalDate.of(2025, 1, 1),
                                LocalDate.of(2025, 12, 31));

                BigDecimal cost = tariff.calculateUsageCost(new BigDecimal("10")); // 10 kWh
                assertEquals(new BigDecimal("3.00"), cost.setScale(2, RoundingMode.HALF_UP));
        }

        @Test
        void gas_singleRate_costUsesConversionThenRate() {
                Tariff tariff = new Tariff(
                                "Gas Standard",
                                FuelType.GAS,
                                RateType.SINGLE_RATE,
                                new BigDecimal("0.10"),
                                new BigDecimal("0.30"),
                                new BigDecimal("0.20"),
                                LocalDate.of(2025, 1, 1),
                                LocalDate.of(2025, 12, 31));

                BigDecimal usageFt3 = new BigDecimal("100");

                // Expected according to your Tariff.calculateCost()
                BigDecimal usageInM3 = usageFt3.multiply(Tariff.FT3_TO_M3);
                BigDecimal usageInKwh = usageInM3
                                .multiply(Tariff.GAS_CORRECTION_FACTOR)
                                .multiply(Tariff.GAS_CALORIFIC_VALUE)
                                .divide(new BigDecimal("3.6"), 10, RoundingMode.HALF_UP);

                BigDecimal expected = usageInKwh.multiply(new BigDecimal("0.10"));

                BigDecimal actual = tariff.calculateUsageCost(usageFt3);

                assertEquals(
                                expected.setScale(8, RoundingMode.HALF_UP),
                                actual.setScale(8, RoundingMode.HALF_UP));
        }

        @Test
        void toDisplay_containsIdNameFuelAndRateType() {
                Tariff tariff = new Tariff(
                                "Elec Standard",
                                FuelType.ELECTRICITY,
                                RateType.SINGLE_RATE,
                                new BigDecimal("0.30"),
                                new BigDecimal("0.50"),
                                new BigDecimal("0.20"),
                                LocalDate.of(2025, 1, 1),
                                LocalDate.of(2025, 12, 31));

                String display = tariff.toDisplay();

                assertTrue(display.contains(tariff.getId()));
                assertTrue(display.contains("Elec Standard"));
                assertTrue(display.contains("ELECTRICITY"));
                assertTrue(display.contains("SINGLE"));
        }
}
