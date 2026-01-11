package backend.repositories;

import backend.models.MeterReading;

public class MeterReadingRepository extends BaseRepository<MeterReading> {

    public MeterReadingRepository() {
        super("meter_readings");
    }

    public String generateId() {
        return "MTR" + this.generateId(7);
    }

}
