package backend.repositories;

import java.util.List;

import backend.models.Database;
import backend.models.Tariff;

public class TariffRepository extends BaseRepository<Tariff> {
    @Override
    protected void setCollection(Database db, List<Tariff> items) {
        db.tariffs = items;
    }

    @Override
    protected List<Tariff> getCollection(Database db) {
        return db.tariffs;
    }

    public String generateId() {
        return "TAR" + this.generateId(5);
    }

}
