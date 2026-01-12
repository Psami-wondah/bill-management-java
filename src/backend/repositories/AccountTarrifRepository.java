package backend.repositories;

import backend.models.AccountTariff;

public class AccountTarrifRepository extends BaseRepository<AccountTariff> {

    public AccountTarrifRepository() {
        super("account_tarrifs");
    }

    public String generateId() {
        return "ATF" + this.generateId(5);
    }
}
