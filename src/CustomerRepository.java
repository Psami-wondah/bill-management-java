
import java.util.Optional;

public class CustomerRepository extends FileRepository<Customer> {
    public CustomerRepository() {
        super("data/customers.dat");
    }

    @Override
    public String getId(Customer customer) {
        return customer.getId();
    }

    // You can add custom query methods:
    public Optional<Customer> findByEmail(String email) {
        return findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
