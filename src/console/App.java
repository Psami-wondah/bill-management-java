package console;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import backend.enums.FuelType;
import backend.enums.PaymentMethod;
import backend.enums.RateType;
import backend.enums.ReadingType;
import backend.enums.RegisterType;
import backend.models.Account;
import backend.models.AccountTariff;
import backend.models.Customer;
import backend.models.Meter;
import backend.models.MeterReading;
import backend.models.Tariff;

public class App {
    static Scanner scanner;

    public static final String PASSWORD = "admin";

    public static void showDashboard() {
        System.out.println("Welcome to your dashboard!");
        // Further dashboard implementation goes here
        System.out.println("What would you like to do today?");
        System.out.println("1. Register a new customer");
        System.out.println("2. Manage Customers");
        System.out.println("3. Manage Tariffs");
        System.out.println("4. Exit");

        System.out.print("Please enter your choice (1-4): ");

        scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        System.out.println();
        switch (choice) {
            case 1:
                if (Common.checkIfTariffsExist()) {
                    registerCustomer();
                } else {
                    System.out.println(
                            "No tariffs found. Please add tariffs before registering customers.");
                    manageTariffs();
                }
                showDashboard();
                break;
            case 2:
                manageCustomers();
                break;
            case 3:
                manageTariffs();
                break;

            case 4:
                System.out.println("Exiting the application. Goodbye!");
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please try again.");
                showDashboard();
        }

    }

    public static Account selectAccountForCustomer(Customer customer) {
        scanner = new Scanner(System.in);
        ArrayList<Account> accounts = Account.objects.filter(a -> a.getCustomerId().equals(customer.getId()));
        if (accounts.isEmpty()) {
            System.out.println("No accounts found for this customer.");
            return null;
        }
        System.out.println("Available Accounts:");
        for (Account account : accounts) {
            System.out.println(account.getId());
        }
        System.out.print("Select Account ID: ");
        String accountId = scanner.nextLine();
        Account account = Account.objects.findById(accountId).orElse(null);
        if (account == null) {
            System.out.println("Invalid Account ID.");
            return selectAccountForCustomer(customer);
        }
        return account;
    }

    public static Tariff chooseTariff(Meter meter) {
        scanner = new Scanner(System.in);
        System.out.println("Available Tariffs:");
        ArrayList<Tariff> tariffs = Tariff.objects.filter(t -> t.getFuelType().equals(meter.getFuelType()));
        if (tariffs.isEmpty()) {
            System.out.println("No tariffs available for the selected fuel type.");
            addNewTariff();
        }
        int index = 1;
        for (Tariff tariff : tariffs) {
            System.out.println(index + ". " + tariff.getName() + " - " + tariff.getFuelType() + " - "
                    + tariff.getRateType());
            index++;
        }
        System.out.print("Enter the number of the tariff to assign: ");
        int choice = scanner.nextInt();
        if (choice < 1 || choice > tariffs.size()) {
            System.out.println("Invalid choice. Please try again.");
            return chooseTariff(meter);
        }
        Tariff tariff = tariffs.get(choice - 1);
        if (tariff.getFuelType() != meter.getFuelType()) {
            System.out.println("Selected tariff fuel type does not match meter fuel type. Please choose again.");
            return chooseTariff(meter);
        }
        if (tariff.getRateType() == RateType.TWO_RATE
                && meter.getRegisterType() != RegisterType.TWO_REGISTER) {
            System.out.println("Meter register type does not support dual rate tariff. Please choose again.");
            return chooseTariff(meter);
        }
        if (tariff.getEffectiveFrom().isAfter(meter.getInstallationDate())) {
            System.out.println("Tariff is not effective from the meter installation date: "
                    + meter.getInstallationDate().toString() + ". Please choose again.");
            return chooseTariff(meter);
        }
        return tariff;
    }

    public static AccountTariff assignTariffToCustomer(Customer customer, Meter meter) {
        Account account = Account.objects.findById(meter.getAccountId()).orElse(null);
        if (account == null) {
            return null;
        }
        Tariff tariff = chooseTariff(meter);

        AccountTariff newAccountTariff = new AccountTariff(account.getId(), tariff.getId(),
                meter.getInstallationDate(), null);

        newAccountTariff.save();
        return newAccountTariff;
    }

    public static void assignMeterToCustomer(Customer customer, Account account) {
        FuelType fuelType = Common.chooseFuelType();
        scanner = new Scanner(System.in);
        System.out.print("Installation Date (YYYY-MM-DD): ");
        String installationDateInput = scanner.nextLine();
        LocalDate installationDate = LocalDate.parse(installationDateInput);
        Meter newMeter = new Meter(account.getId(), fuelType, Common.chooseRegisterType(fuelType), installationDate);

        newMeter.save();

        MeterReading initialReading = new MeterReading(newMeter.getId(), installationDate, BigDecimal.ZERO,
                ReadingType.INITIAL);
        initialReading.save();
        assignTariffToCustomer(customer, newMeter);
        System.out.println("Meter assigned successfully with ID: " + newMeter.getId());
    }

    public static Customer searchCustomers() {
        scanner = new Scanner(System.in);
        System.out.println("=========================================");
        System.out.println("Search Customers");
        System.out.println("=========================================");
        System.out.println();
        System.out.print("Enter customer name or ID to search: ");
        String query = scanner.nextLine();

        List<Customer> results = Customer.objects.search(query);

        if (results.isEmpty()) {
            System.out.println("No customers found matching the query: " + query);
            return null;
        } else {
            System.out.println("Search Results:");
            return listCustomers(1, results);
        }
    }

    public static Customer listCustomers(int page, List<Customer> inputCustomers) {
        int pageSize = 10;
        int totalCustomers = inputCustomers.size();
        List<Customer> customers = inputCustomers.subList((page - 1) * pageSize,
                Math.min(page * pageSize, totalCustomers));
        System.out.println("Customers:");
        for (Customer customer : customers) {
            System.out.println(customer.getId() + ": " + customer.getName());
        }
        if ((page * pageSize) < totalCustomers) {
            System.out.println("N. Next Page");
        }

        if (page > 1) {
            System.out.println("P. Previous Page");
        }

        System.out.println("B. Back");

        System.out.print("Enter customer id to select, N for next page, P for previous page, or B to go back: ");
        scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("N") && (page * pageSize) < totalCustomers) {
            return listCustomers(page + 1, inputCustomers);
        } else if (input.equalsIgnoreCase("P") && page > 1) {
            return listCustomers(page - 1, inputCustomers);
        } else if (input.equalsIgnoreCase("B")) {
            manageCustomers();
            return null;
        } else {
            Customer selectedCustomer = Customer.objects.findById(input).orElse(null);
            if (selectedCustomer == null) {
                System.out.println("Invalid customer ID. Please try again.");
                return listCustomers(page, inputCustomers);
            } else {
                System.out.println("Selected Customer: " + selectedCustomer.getName());
                return selectedCustomer;
            }
        }

    }

    public static void manageCustomers() {

        System.out.println("=========================================");
        System.out.println("Manage Customers");
        System.out.println("=========================================");
        System.out.println();

        System.out.println("What would you like to do?");
        System.out.println("1. List Customers");
        System.out.println("2. Search Customers");
        System.out.println("3. Back to Main Menu");

        System.out.print("Please enter your choice (1-3): ");

        scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        System.out.println();

        Customer customer;

        switch (choice) {
            case 1:
                customer = listCustomers(1, Customer.objects.findAll());
                if (customer == null) {
                    manageCustomers();
                    return;
                }
                break;
            case 2:
                customer = searchCustomers();
                if (customer == null) {
                    manageCustomers();
                    return;
                }
                break;
            case 3:
                showDashboard();
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
                manageCustomers();
                return;
        }

        System.out.println("What would you like to for " + customer.getName() + "?");
        System.out.println("1. Assign a Meter");
        System.out.println("2. Add an Account");
        System.out.println("3. View Account Summmary");
        System.out.println("4. Enter meter readings");
        System.out.println("5. Record a payment");
        System.out.println("6. Generate Invoice");
        System.out.println("7. Back to Customer Management");
        System.out.println("8. Exit");

        System.out.print("Please enter your choice (1-8): ");
        choice = scanner.nextInt();
        System.out.println();

        switch (choice) {
            case 1:
                Account account = selectAccountForCustomer(customer);
                if (account != null) {
                    assignMeterToCustomer(customer, account);
                }
                break;
            case 2:
                PaymentMethod paymentMethod = Common.choosePaymentMethod();
                Account newAccount = new Account(customer.getId(), paymentMethod);
                newAccount.save();
                System.out.println("New account created with ID: " + newAccount.getId());
                break;
            case 3:
                // View Account Summary logic here
                System.out.println("Feature not implemented yet.");
                break;
            case 4:
                Account acc = selectAccountForCustomer(customer);
                if (acc != null) {
                    enterMeterReadings(acc);
                }
                break;
            case 5:
                // Record a payment logic here
                System.out.println("Feature not implemented yet.");
                break;
            case 6:
                // Generate Invoice logic here
                System.out.println("Feature not implemented yet.");
                break;
            case 7:
                manageCustomers();
                return;
            case 8:
                System.out.println("Exiting the application. Goodbye!");
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    public static void enterMeterReadings(Account account) {
        scanner = new Scanner(System.in);

        System.out.println("=========================================");
        System.out.println("Enter Meter Readings!");
        System.out.println("=========================================");
        System.out.println();

        ArrayList<Meter> meters = Meter.objects.filter(m -> m.getAccountId().equals(account.getId()));
        if (meters.isEmpty()) {
            System.out.println("No meters found for this account.");
            enterMeterReadings(account);
            return;
        }
        System.out.println("Available Meters:");
        for (Meter meter : meters) {
            System.out.println(meter.getId() + ": " + meter.getFuelType() + " - " + meter.getRegisterType());
        }
        System.out.print("Select Meter ID: ");
        String meterId = scanner.nextLine();
        Meter meter = Meter.objects.findById(meterId).orElse(null);
        if (meter == null) {
            System.out.println("Invalid Meter ID.");
            enterMeterReadings(account);
            return;
        }
        System.out.print("Enter Reading Date (YYYY-MM-DD): ");
        String readingDate = scanner.nextLine();
        LocalDate date = LocalDate.parse(readingDate);

        MeterReading existingReading = MeterReading.objects.findLatestReadingByMeterId(meter.getId());
        if (existingReading != null && !date.isAfter(existingReading.getDate())) {
            System.out.println("New reading date must be after the last reading date: "
                    + existingReading.getDate().toString());
            enterMeterReadings(account);
            return;
        }

        if (meter.getRegisterType() == RegisterType.SINGLE_REGISTER) {
            System.out.print("Enter Reading: ");
            BigDecimal reading = scanner.nextBigDecimal();

            MeterReading meterReading = new MeterReading(meter.getId(), date, reading,
                    ReadingType.CUSTOMER);
            meterReading.save();

            System.out.println("Single register reading of " + reading + " saved for meter " + meter.getId());
        } else {
            System.out.print("Enter Day Reading: ");
            BigDecimal dayReading = scanner.nextBigDecimal();
            System.out.print("Enter Night Reading: ");
            BigDecimal nightReading = scanner.nextBigDecimal();
            // Save dual register reading logic here
            MeterReading meterReading = new MeterReading(meter.getId(), date, dayReading, nightReading,
                    ReadingType.CUSTOMER);
            meterReading.save();
            System.out.println("Dual register readings of Day: " + dayReading + " and Night: " + nightReading
                    + " saved for meter " + meter.getId());
        }

    }

    public static Customer registerCustomer() {
        scanner = new Scanner(System.in);

        System.out.println("=========================================");
        System.out.println("Register New Customer!");
        System.out.println("=========================================");
        System.out.println();
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone Number: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Address: ");
        String address = scanner.nextLine();

        PaymentMethod paymentMethod = Common.choosePaymentMethod();
        Customer newCustomer = new Customer(name, email, phoneNumber, address);
        Account newAccount = new Account(newCustomer.getId(), paymentMethod);
        newAccount.save();
        newCustomer.save();
        assignMeterToCustomer(newCustomer, newAccount);

        System.out.println("Customer Added Successfully: " + newCustomer.getId());
        return newCustomer;
    }

    public static void addNewTariff() {

        System.out.println("=========================================");
        System.out.println("Add New Tariff");
        System.out.println("=========================================");
        System.out.println();
        FuelType fuelType = Common.chooseFuelType();
        RateType rateType;
        if (fuelType == FuelType.GAS) {
            System.out.println("Selected fuel type GAS. Defaulting to Single Rate tariff.");
            rateType = RateType.SINGLE_RATE;
        } else {
            rateType = Common.chooseRateType();
        }
        System.out.print("Name: ");
        scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        BigDecimal singleRate = null;
        BigDecimal dayRate = null;
        BigDecimal nightRate = null;
        if (rateType == RateType.SINGLE_RATE) {
            System.out.print("Single Rate (per unit): ");
            singleRate = scanner.nextBigDecimal();
        } else {
            System.out.print("Day Rate (per unit): ");
            dayRate = scanner.nextBigDecimal();
            System.out.print("Night Rate (per unit): ");
            nightRate = scanner.nextBigDecimal();
        }
        System.out.print("Daily Standing Charge: ");
        BigDecimal dailyStandingCharge = scanner.nextBigDecimal();
        System.out.print("VAT Rate (as percentage, e.g., enter 20 for 20%): ");
        BigDecimal vatRateInput = scanner.nextBigDecimal();
        BigDecimal vatRate = vatRateInput.divide(BigDecimal.valueOf(100));
        System.out.print("Effective From (YYYY-MM-DD): ");
        String effectiveFromInput = scanner.next();
        System.out.print("Effective To (YYYY-MM-DD): ");
        String effectiveToInput = scanner.next();
        LocalDate effectiveFrom = LocalDate.parse(effectiveFromInput);
        LocalDate effectiveTo = LocalDate.parse(effectiveToInput);

        if (effectiveTo.isBefore(effectiveFrom)) {
            System.out
                    .println("Effective To date cannot be before Effective From date. Please try again.");
            addNewTariff();
            return;
        }

        Tariff newTariff;

        if (rateType == RateType.SINGLE_RATE) {
            newTariff = new Tariff(name, fuelType, rateType, singleRate, dailyStandingCharge, vatRate,
                    effectiveFrom, effectiveTo);
        } else {
            newTariff = new Tariff(name, fuelType, rateType, dayRate, nightRate, dailyStandingCharge, vatRate,
                    effectiveFrom, effectiveTo);
        }
        newTariff.save();
        System.out.println("Tariff Added Successfully: " + newTariff.getId());

    }

    public static void updateExistingTariff() {
        // TODO: Implement update logic
        scanner = new Scanner(System.in);
        System.out.println("=========================================");
        System.out.println("Update Existing Tariff");
        System.out.println("=========================================");
        System.out.println();
        Tariff tariff = Common.getTariffToUpdate();
        System.out.println("Updating Tariff: " + tariff.getName());
        System.out.print("New Name (current: " + tariff.getName() + "): ");
        String name = scanner.nextLine();
        tariff.setName(name);
        tariff.save();
        System.out.println("Tariff Updated Successfully: " + tariff.getId());
    }

    public static void manageTariffs() {
        System.out.println("=========================================");
        System.out.println("Manage Tariffs");
        System.out.println("=========================================");

        ArrayList<Tariff> tariffs = Tariff.objects.findAll();

        System.out.println("Current Tariffs:");
        for (Tariff tariff : tariffs) {
            System.out.println(tariff.getName() + " - " + tariff.getFuelType() + " - " + tariff.getRateType());
        }
        System.out.println();

        System.out.println("What would you like to do?");

        System.out.println();
        System.out.println("1. Add New Tariff");
        System.out.println("2. Update Existing Tariff");
        System.out.println("3. Return to Dashboard");

        System.out.print("Please enter your choice (1-3): ");

        scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                addNewTariff();
                break;
            case 2:
                updateExistingTariff();
                break;
            case 3:
                showDashboard();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                manageTariffs();
        }

    }

    public static void intro() {
        System.out.println("=========================================");
        System.out.println("Welcome to the Bill Management System!");
        System.out.println("Manage your customers and tariffs with ease.");
        System.out.println("=========================================");
        System.out.println();
        System.out.print("Enter the password to continue: ");
        scanner = new Scanner(System.in);
        String password = scanner.nextLine();
        System.out.println();
        if (!password.equals(PASSWORD)) {
            System.out.println("Incorrect password. Try again.");
            intro();
            return;
        }
        showDashboard();
    }

    public static void main(String[] args) throws Exception {
        intro();
        scanner.close();
    }
}
