package backend.repositories;

import java.util.Optional;

import backend.models.Customer;

public class CustomerRepository extends BaseRepository<Customer> {
    public CustomerRepository() {
        super("customers");
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
}
