package backend.repositories;

import backend.models.Account;

public class AccountRepository extends BaseRepository<Account> {
    public AccountRepository() {
        super("accounts");
    }

    public String generateId() {
        return "ACC" + this.generateId(5);
    }

}
