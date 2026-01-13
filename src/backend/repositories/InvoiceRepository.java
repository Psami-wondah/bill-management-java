package backend.repositories;

import java.util.List;
import backend.models.Database;
import backend.models.Invoice;

public class InvoiceRepository extends BaseRepository<Invoice> {

    @Override
    protected void setCollection(Database db, List<Invoice> items) {
        db.invoices = items;
    }

    @Override
    protected List<Invoice> getCollection(Database db) {
        return db.invoices;
    }

    public String generateId() {
        return "INV" + this.generateId(7);
    }

}
