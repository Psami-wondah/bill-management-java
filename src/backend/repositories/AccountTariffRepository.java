package backend.repositories;

import java.util.List;
import backend.models.AccountTariff;
import backend.models.Database;

public class AccountTariffRepository extends BaseRepository<AccountTariff> {

    @Override
    protected void setCollection(Database db, List<AccountTariff> items) {
        db.account_tariffs = items;
    }

    @Override
    protected List<AccountTariff> getCollection(Database db) {
        return db.account_tariffs;
    }

    public String generateId() {
        return "ATF" + this.generateId(5);
    }
}
