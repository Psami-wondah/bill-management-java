package backend.repositories;

import backend.models.Payment;

public class PaymentRepository extends BaseRepository<Payment> {

    public PaymentRepository() {
        super("payments");
    }

    public String generateId() {
        return "PAY" + this.generateId(7);
    }

}
