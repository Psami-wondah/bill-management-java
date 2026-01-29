package test.backend.models;

import backend.models.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import backend.enums.FuelType;
import backend.enums.RegisterType;

class MeterTest {

    @Test
    void testMeterCreationWithoutId() {
        String accountId = "ACC002";
        FuelType fuelType = FuelType.GAS;
        RegisterType registerType = RegisterType.SINGLE_REGISTER;
        LocalDate installationDate = LocalDate.now();

        Meter meter = new Meter(accountId, fuelType, registerType, installationDate);

        assertNotNull(meter.getId());
        assertEquals(accountId, meter.getAccountId());
        assertEquals(fuelType, meter.getFuelType());
        assertEquals(registerType, meter.getRegisterType());
        assertEquals(installationDate, meter.getInstallationDate());
    }

    @Test
    void testToDisplay() {
        Meter meter = new Meter("MTR123", "ACC004", FuelType.GAS, RegisterType.SINGLE_REGISTER, LocalDate.now());

        String display = meter.toDisplay();

        assertTrue(display.contains("MTR123"));
        assertTrue(display.contains("GAS"));
        assertTrue(display.contains("SINGLE_REGISTER"));
    }

}