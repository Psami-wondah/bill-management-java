package backend.repositories;

import java.util.List;

import backend.models.Database;
import backend.models.Meter;

public class MeterRepository extends BaseRepository<Meter> {

    @Override
    protected void setCollection(Database db, List<Meter> items) {
        db.meters = items;
    }

    @Override
    protected List<Meter> getCollection(Database db) {
        return db.meters;
    }

    public String generateId() {
        return "MET" + this.generateId(5);
    }

}
