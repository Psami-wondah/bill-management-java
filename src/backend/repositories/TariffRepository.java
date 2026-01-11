package backend.repositories;

import backend.models.Tariff;

public class TariffRepository extends BaseRepository<Tariff> {

    public TariffRepository() {
        super("tarrifs");
    }

    public String generateId() {
        return "TAR" + this.generateId(5);
    }

}
