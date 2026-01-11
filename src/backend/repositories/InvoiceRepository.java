package backend.repositories;

import backend.models.Invoice;

public class InvoiceRepository extends BaseRepository<Invoice> {

    public InvoiceRepository() {
        super("invoices");
    }

    public String generateId() {
        return "INV" + this.generateId(7);
    }

}
