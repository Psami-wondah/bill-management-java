package backend.models;

import java.math.BigDecimal;
import java.util.List;

import backend.enums.InvoiceStatus;
import backend.repositories.InvoiceRepository;

public class Invoice implements BaseModel {

    public static final InvoiceRepository objects = new InvoiceRepository();

    private final String id;
    private String accountId;
    private String accountTariffId;
    private BigDecimal subTotal;
    private BigDecimal vat;
    private BigDecimal total;
    private InvoiceStatus status;
    private String periodStart;
    private String periodEnd;
    private List<InvoiceItem> items;

    public Invoice(String accountId, String accountTariffId, BigDecimal subTotal, BigDecimal vat,
            BigDecimal total, InvoiceStatus status, String periodStart, String periodEnd, List<InvoiceItem> items) {
        this.id = objects.generateId();
        this.accountId = accountId;
        this.accountTariffId = accountTariffId;
        this.subTotal = subTotal;
        this.vat = vat;
        this.total = total;
        this.status = status;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountTariffId() {
        return accountTariffId;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void save() {
        objects.add(this);
    }
}