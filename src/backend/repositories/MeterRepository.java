package backend.repositories;

import backend.models.Meter;

public class MeterRepository extends BaseRepository<Meter> {

    public MeterRepository() {
        super("meters");
    }

    public String generateId() {
        return "MET" + this.generateId(5);
    }

}
