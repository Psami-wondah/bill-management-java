package backend.repositories;

import java.util.Comparator;

import backend.models.MeterReading;

public class MeterReadingRepository extends BaseRepository<MeterReading> {

    public MeterReadingRepository() {
        super("meter_readings");
    }

    public String generateId() {
        return "MTR" + this.generateId(7);
    }

    public MeterReading findLatestReadingByMeterId(String meterId) {
        return this.filter(r -> r.getMeterId().equals(meterId))
                .stream()
                .max(Comparator.comparing(MeterReading::getDate))
                .orElse(null);
    }

}
