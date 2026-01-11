package backend.repositories;

import backend.models.AccountTarrif;

public class AccountTarrifRepository extends BaseRepository<AccountTarrif> {

    public AccountTarrifRepository() {
        super("account_tarrifs");
    }

    public String generateId() {
        return "ATF" + this.generateId(5);
    }
}
