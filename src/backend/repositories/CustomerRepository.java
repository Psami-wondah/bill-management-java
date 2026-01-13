package backend.repositories;

import java.util.List;
import java.util.Optional;

import backend.models.Customer;
import backend.models.Database;

public class CustomerRepository extends BaseRepository<Customer> {
    @Override
    protected void setCollection(Database db, List<Customer> items) {
        db.customers = items;
    }

    @Override
    protected List<Customer> getCollection(Database db) {
        return db.customers;
    }

    // You can add custom query methods:
    public Optional<Customer> findByEmail(String email) {
        return findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public String generateId() {
        return "CUS" + this.generateId(5);
    }

    public List<Customer> search(String query) {
        return filter(c -> c.getName().toLowerCase().contains(query.toLowerCase())
                || c.getId().equalsIgnoreCase(query));
    }
}
