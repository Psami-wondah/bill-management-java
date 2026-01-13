package backend.repositories;

import java.util.List;

import backend.models.Database;
import backend.models.Payment;

public class PaymentRepository extends BaseRepository<Payment> {

    @Override
    protected void setCollection(Database db, List<Payment> items) {
        db.payments = items;
    }

    @Override
    protected List<Payment> getCollection(Database db) {
        return db.payments;
    }

    public String generateId() {
        return "PAY" + this.generateId(7);
    }

}
