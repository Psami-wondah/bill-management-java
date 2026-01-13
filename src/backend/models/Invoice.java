package backend.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private List<InvoiceItem> items;

    @JsonCreator
    public Invoice(@JsonProperty("id") String id,
            @JsonProperty("accountId") String accountId,
            @JsonProperty("accountTariffId") String accountTariffId,
            @JsonProperty("subTotal") BigDecimal subTotal,
            @JsonProperty("vat") BigDecimal vat,
            @JsonProperty("total") BigDecimal total,
            @JsonProperty("status") InvoiceStatus status,
            @JsonProperty("periodStart") LocalDate periodStart,
            @JsonProperty("periodEnd") LocalDate periodEnd,
            @JsonProperty("items") List<InvoiceItem> items) {
        this.id = id;
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

    public Invoice(String accountId, String accountTariffId, BigDecimal subTotal, BigDecimal vat,
            BigDecimal total, InvoiceStatus status, LocalDate periodStart, LocalDate periodEnd,
            List<InvoiceItem> items) {
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

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void save() {
        objects.add(this);
    }
}