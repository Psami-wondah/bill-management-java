package backend.models;

import java.util.ArrayList;
import java.util.List;

public class Database {
    public List<Customer> customers = new ArrayList<>();
    public List<Account> accounts = new ArrayList<>();
    public List<Meter> meters = new ArrayList<>();
    public List<MeterReading> meter_readings = new ArrayList<>();
    public List<Tariff> tariffs = new ArrayList<>();
    public List<Invoice> invoices = new ArrayList<>();
    public List<AccountTariff> account_tariffs = new ArrayList<>();
    public List<Payment> payments = new ArrayList<>();
}
