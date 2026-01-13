package backend.repositories;

import java.util.List;

import backend.models.Account;
import backend.models.Database;

public class AccountRepository extends BaseRepository<Account> {
    @Override
    protected void setCollection(Database db, List<Account> items) {
        db.accounts = items;
    }

    @Override
    protected List<Account> getCollection(Database db) {
        return db.accounts;
    }

    public String generateId() {
        return "ACC" + this.generateId(5);
    }

}
